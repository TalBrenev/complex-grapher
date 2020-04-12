(ns complex-grapher.webgl
  (:require [clojure.string :as s]
            [complex-grapher.complex-arithmetic :refer [re im]]
            [complex-grapher.utils :refer [width height]]))

(defn detect-webgl [canvas-id]
  (let [gl (.getContext (.getElementById js/document canvas-id) "webgl")]
    (and gl (instance? js/WebGLRenderingContext gl))))

(def vs-src
  "attribute vec4 aVertexPosition;
   varying highp float x;
   varying highp float y;
   void main() {  gl_Position = aVertexPosition; x = aVertexPosition[0]; y = aVertexPosition[1]; }")

(defn ast->glsl [ast]
  (if (map? ast)
    (case (:token ast)
      "z"  "z"
      "e"  "vec2(exp(1.0),0.0)"
      "pi" "vec2(radians(180.0),0.0)"
      "i"  "vec2(0.0,1.0)"
      (str "vec2(float(" (re (:value ast)) "), float(" (im (:value ast)) "))"))
    (let [funcName (str "comp" (s/capitalize
                                 (case (:token (first ast))
                                   "+" "add"
                                   "*" "mul"
                                   "/" "div"
                                   "^" "pow"
                                   "-" (if (= (:type (first ast)) :function) "negate" "sub")
                                   (:token (first ast)))))]
      (str funcName "(" (s/join "," (map ast->glsl (rest ast))) ")"))))

(defn fs-src [ast modulus left-x right-x top-y bottom-y]
  (str "
   varying highp float x;
   varying highp float y;

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

   highp float arg(highp vec2 z) {
     return atan(z[1], z[0]);
   }

   highp float mag(highp vec2 z) {
     return length(z);
   }

   highp vec2 toCart(highp float a, highp float m) {
     return vec2(m*cos(a), m*sin(a));
   }

   highp vec2 compRe(highp vec2 z) {
     return vec2(z[0], 0.0);
   }

   highp vec2 compIm(highp vec2 z) {
     return vec2(z[1], 0.0);
   }

   highp vec2 compArg(highp vec2 z) {
     return vec2(arg(z), 0.0);
   }

   highp vec2 compMag(highp vec2 z) {
     return vec2(mag(z), 0.0);
   }

   highp vec2 compAdd(highp vec2 z1, highp vec2 z2) {
     return vec2(z1[0] + z2[0], z1[1] + z2[1]);
   }

   highp vec2 compSub(highp vec2 z1, highp vec2 z2) {
     return vec2(z1[0] - z2[0], z1[1] - z2[1]);
   }

   highp vec2 compMul(highp vec2 z1, highp vec2 z2) {
     return toCart(arg(z1)+arg(z2), mag(z1)*mag(z2));
   }

   highp vec2 compDiv(highp vec2 z1, highp vec2 z2) {
     if (mag(z2) == 0.0) {
       return vec2(0.0, 0.0);
     }
     else {
       return toCart(arg(z1)-arg(z2), mag(z1)/mag(z2));
     }
   }

   highp vec2 compNegate(highp vec2 z) {
     return compSub(vec2(0.0, 0.0), z);
   }

   highp vec2 compPow(highp vec2 z1, highp vec2 z2) {
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
   }

   highp vec2 compSin(highp vec2 z) {
     highp vec2 a = compPow(vec2(exp(1.0),0.0), compMul(vec2(0.0,1.0),z));
     return compDiv(compSub(a, compDiv(vec2(1.0,0.0), a)), compMul(vec2(2.0,0.0), vec2(0.0,1.0)));
   }

   highp vec2 compCos(highp vec2 z) {
     highp vec2 a = compPow(vec2(exp(1.0),0.0), compMul(vec2(0.0,1.0),z));
     return compDiv(compAdd(a, compDiv(vec2(1.0,0.0), a)), vec2(2.0,0.0));
   }

   highp vec2 compTan(highp vec2 z) {
     highp vec2 s = compSin(z);
     highp vec2 c = compCos(z);
     if (mag(c) == 0.0) {
       return vec2(0.0, 0.0);
     }
     else {
       return compDiv(s, c);
     }
   }

   highp vec2 compLog(highp vec2 z) {
     if (mag(z) == 0.0) {
       return vec2(0.0, 0.0);
     }
     else {
       return vec2(log(mag(z)), arg(z));
     }
   }

   highp vec2 compLn(highp vec2 z) {
     return compLog(z);
   }

   void main()
   {
     highp vec2 z = vec2(
       float("(/ (- right-x left-x) 2)") * x + float("(/ (+ left-x right-x) 2)"),
       float("(/ (- bottom-y top-y) 2)") * y + float("(/ (+ top-y bottom-y) 2)"));

     highp vec2 f = "(ast->glsl ast)";

     highp float modulus = float(" modulus ");
     highp float h = -floor(degrees(arg(f))) + 180.0;
     highp float v = mod(mag(f), modulus) / modulus;
     if (mod(mag(f), 2.0*modulus) > modulus) {
       v = 1.0 - v;
     }
     gl_FragColor = hsvToRgb(h, 1.0, v);
   }
   "))

(defn create-context [canvas-id]
  (let [gl (-> (.getElementById js/document canvas-id)
               (.getContext "webgl"))]
    (.clearColor gl 0 0 0 1)
    gl))

(defn create-shader [gl type src]
  (let [shader (.createShader gl type)]
    (.shaderSource gl shader src)
    (.compileShader gl shader)
    ;(.log js/console (.getShaderInfoLog gl shader))
    shader))

(defn create-shader-program [gl vs-src fs-src]
  (let [vs      (create-shader gl (.-VERTEX_SHADER gl) vs-src)
        fs      (create-shader gl (.-FRAGMENT_SHADER gl) fs-src)
        program (.createProgram gl)]
    (.attachShader gl program vs)
    (.attachShader gl program fs)
    (.linkProgram gl program)
    program))

(defn create-buffer [gl]
  (let [buffer (.createBuffer gl)]
    (.bindBuffer gl (.-ARRAY_BUFFER gl) buffer)
    (.bufferData gl (.-ARRAY_BUFFER gl) (js/Float32Array. #js [-1 1 1 1 -1 -1 1 -1]) (.-STATIC_DRAW gl))
    buffer))

(defn draw [canvas-id ast modulus left-x right-x top-y bottom-y]
  (let [gl (create-context canvas-id)
        program (create-shader-program gl vs-src (fs-src ast modulus left-x right-x top-y bottom-y))
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
