(ns complex-grapher.webgl
  (:require [clojure.string :as s]
            [complex-grapher.parser :as parser]
            [complex-grapher.complex-arithmetic :refer [re im]]
            [complex-grapher.utils :refer [set-attr width height]]))

;;------------------------------------------------------------------------------;;

(def ^:private webgl-utility-funcs
  "
  highp float arg(highp vec2 z) {
    return atan(z[1], z[0]);
  }

  highp float mag(highp vec2 z) {
    return length(z);
  }

  highp vec2 toCart(highp float a, highp float m) {
    return vec2(m*cos(a), m*sin(a));
  }
  ")

(def ^:private webgl-consts
  {:z  "z"
   :e  "vec2(exp(1.0),0.0)"
   :pi "vec2(radians(180.0),0.0)"
   :i  "vec2(0.0,1.0)"})

(def ^:private webgl-funcs
  [{:token :re
    :args ["z"]
    :body "
          return vec2(z[0], 0.0);
          "}

   {:token :im
    :args ["z"]
    :body "
          return vec2(z[1], 0.0);
          "}

   {:token :arg
    :args ["z"]
    :body "
          return vec2(arg(z), 0.0);
          "}

   {:token :mag
    :args ["z"]
    :body "
          return vec2(mag(z), 0.0);
          "}

   {:token :add
    :args ["z1", "z2"]
    :body "
          return vec2(z1[0] + z2[0], z1[1] + z2[1]);
          "}

   {:token :sub
    :args ["z1", "z2"]
    :body "
          return vec2(z1[0] - z2[0], z1[1] - z2[1]);
          "}

   {:token :mul
    :args ["z1", "z2"]
    :body "
          return toCart(arg(z1)+arg(z2), mag(z1)*mag(z2));
          "}

   {:token :div
    :args ["z1", "z2"]
    :body "
          if (mag(z2) == 0.0) {
            return vec2(0.0, 0.0);
          }
          else {
            return toCart(arg(z1)-arg(z2), mag(z1)/mag(z2));
          }
          "}

   {:token :negate
    :args ["z"]
    :body "
          return {{sub}}(vec2(0.0, 0.0), z);
          "}

   {:token :pow
    :args ["z1", "z2"]
    :body "
          if (mag(z2) == 0.0) {
            return vec2(0.0, 0.0);
          }
          else {
            highp float a = arg(z1);
            highp float b = log(mag(z1));
            highp float c = z2[0];
            highp float d = z2[1];
            return toCart(a*c + b*d, exp(b*c - a*d));
          }
          "}

   {:token :sin
    :args ["z"]
    :body "
          highp vec2 a = {{pow}}(vec2(exp(1.0),0.0), {{mul}}(vec2(0.0,1.0),z));
          return {{div}}({{sub}}(a, {{div}}(vec2(1.0,0.0), a)), {{mul}}(vec2(2.0,0.0), vec2(0.0,1.0)));
          "}

   {:token :cos
    :args ["z"]
    :body "
          highp vec2 a = {{pow}}(vec2(exp(1.0),0.0), {{mul}}(vec2(0.0,1.0),z));
          return {{div}}({{add}}(a, {{div}}(vec2(1.0,0.0), a)), vec2(2.0,0.0));
          "}

   {:token :tan
    :args ["z"]
    :body "
          highp vec2 s = {{sin}}(z);
          highp vec2 c = {{cos}}(z);
          if (mag(c) == 0.0) {
            return vec2(0.0, 0.0);
          }
          else {
            return {{div}}(s, c);
          }
          "}

   {:token :log
    :args ["z"]
    :body "
          if (mag(z) == 0.0) {
            return vec2(0.0, 0.0);
          }
          else {
            return vec2(log(mag(z)), arg(z));
          }
          "}])

;;------------------------------------------------------------------------------;;

(defn- webgl-func-name [token]
  (str "comp" (s/capitalize (name token))))

(defn- generate-webgl-func [func]
  (str
    "highp vec2 "
    (webgl-func-name (:token func))
    "("
    (s/join "," (map #(str "highp vec2 " %) (:args func)))
    ") {"
    (s/replace (:body func) #"\{\{\w*\}\}" #(webgl-func-name (keyword (subs % 2 (- (count %) 2)))))
    "}"))

(defn- ast->str [ast]
  (if (string? ast)
    ast
    (str (first ast) "(" (s/join "," (map ast->str (rest ast))) ")")))

(defn- translate-to-glsl [function]
  (-> function
      (parser/parse)
      (parser/transform-ast
        (merge webgl-consts
               (into {} (mapv
                          #(vector % (webgl-func-name %))
                          (map :token webgl-funcs))))
        #(str "vec2(float("(js/parseFloat %)"), 0.0)"))
      (ast->str)))

(defn- fs-src [function modulus left-x right-x top-y bottom-y]
  (str "
   varying highp float x;
   varying highp float y;

   "webgl-utility-funcs"

   "(s/join "\n" (map generate-webgl-func webgl-funcs))"

   highp vec4 hsvToRgb(highp float h, highp float s, highp float v) {
     highp float c = s * v;
     highp float x = c * (1.0 - abs(mod(h/60.0, 2.0) - 1.0));
     if (0.0 <= h && h <= 60.0) {
       return vec4(c, x, 0.0, 1.0);
     }
     else if (0.0 <= h && h <= 120.0) {
       return vec4(x, c, 0.0, 1.0);
     }
     else if (120.0 <= h && h <= 180.0) {
       return vec4(0.0, c, x, 1.0);
     }
     else if (180.0 <= h && h <= 240.0) {
       return vec4(0.0, x, c, 1.0);
     }
     else if (240.0 <= h && h <= 300.0) {
       return vec4(x, 0.0, c, 1.0);
     }
     else {
       return vec4(c, 0.0, x, 1.0);
     }
   }

   void main() {
     highp vec2 z = vec2(
       float("(/ (- right-x left-x) 2)") * x + float("(/ (+ left-x right-x) 2)"),
       float("(/ (- top-y bottom-y) 2)") * y + float("(/ (+ top-y bottom-y) 2)"));

     highp vec2 f = "(translate-to-glsl function)";

     highp float modulus = float(" modulus ");
     highp float h = mod(degrees(atan(f[1], f[0])), 360.0);
     highp float v = mod(length(f), modulus) / modulus;
     if (mod(mag(f), 2.0*modulus) > modulus) {
       v = 1.0 - v;
     }
     gl_FragColor = hsvToRgb(h, 1.0, v);
   }
   "))

(def ^:private vs-src
  "attribute vec4 aVertexPosition;
   varying highp float x;
   varying highp float y;
   void main() {  gl_Position = aVertexPosition; x = aVertexPosition[0]; y = aVertexPosition[1]; }")

(defn- create-context [canvas-id]
  (let [gl (-> (.getElementById js/document canvas-id)
               (.getContext "webgl"))]
    (.clearColor gl 0 0 0 1)
    gl))

(defn- create-shader [gl type src]
  (let [shader (.createShader gl type)]
    (.shaderSource gl shader src)
    (.compileShader gl shader)
    ;(.log js/console (.getShaderInfoLog gl shader))
    shader))

(defn- create-shader-program [gl vs-src fs-src]
  (let [vs      (create-shader gl (.-VERTEX_SHADER gl) vs-src)
        fs      (create-shader gl (.-FRAGMENT_SHADER gl) fs-src)
        program (.createProgram gl)]
    (.attachShader gl program vs)
    (.attachShader gl program fs)
    (.linkProgram gl program)
    program))

(defn- create-buffer [gl]
  (let [buffer (.createBuffer gl)]
    (.bindBuffer gl (.-ARRAY_BUFFER gl) buffer)
    (.bufferData gl (.-ARRAY_BUFFER gl) (js/Float32Array. #js [-1 1 1 1 -1 -1 1 -1]) (.-STATIC_DRAW gl))
    buffer))

(defn draw [canvas-id function modulus left-x right-x top-y bottom-y]
  (set-attr canvas-id "width" (width canvas-id))
  (set-attr canvas-id "height" (height canvas-id))
  (let [gl (create-context canvas-id)
        program (create-shader-program gl vs-src (fs-src function modulus left-x right-x top-y bottom-y))
        buffer (create-buffer gl)]
    (.viewport gl 0 0 (width canvas-id) (height canvas-id))
    (.clear gl (.-COLOR_BUFFER_BIT gl))
    (.bindBuffer gl (.-ARRAY_BUFFER gl) buffer)
    (.vertexAttribPointer gl
                          (.getAttribLocation gl program "aVertexPosition")
                          2
                          (.-FLOAT gl)
                          false
                          0
                          0)
    (.enableVertexAttribArray gl (.getAttribLocation gl program "aVertexPosition"))
    (.useProgram gl program)
    (.drawArrays gl (.-TRIANGLE_STRIP gl) 0 4)))

(defn detect-webgl [canvas-id]
  (let [gl (.getContext (.getElementById js/document canvas-id) "webgl")]
    (and gl (instance? js/WebGLRenderingContext gl))))
