(ns complex-grapher.webgl
    (:require [complex-grapher.canvas :refer [fix-size width height]]))

(defn create-context [canvas-id]
  (fix-size canvas-id)
  (let [gl (-> (.getElementById js/document canvas-id)
               (.getContext "webgl"))]
    (.clearColor gl 0 0 0 1)
    gl))

(def vs-src
  "attribute vec4 aVertexPosition;
   varying highp float x;
   varying highp float y;
   void main() {  gl_Position = aVertexPosition; x = aVertexPosition[0]; y = aVertexPosition[1]; }")

(defn fs-src [top-left-x top-left-y bottom-right-x bottom-right-y modulus]
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

   void main()
   {
     highp vec2 z = vec2(float("(/ (- bottom-right-x top-left-x) 2)") * x + float("(/ (+ top-left-x bottom-right-x) 2)"), float("(/ (- bottom-right-y top-left-y) 2)") * y + float("(/ (+ top-left-y bottom-right-y) 2)"));

     highp vec2 f = z;

     highp float modulus = float(" modulus ");
     highp float h = -floor(degrees(arg(z))) + 180.0;
     highp float v = mod(mag(z), modulus) / modulus;
     if (mod(mag(z), 2.0*modulus) > modulus) {
       v = 1.0 - v;
     }
     gl_FragColor = hsvToRgb(h, 1.0, v);
   }
   "))

(defn create-shader [gl type src]
  (let [shader (.createShader gl type)]
    (.shaderSource gl shader src)
    (.compileShader gl shader)
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

(defn draw [canvas-id top-left-x top-left-y bottom-right-x bottom-right-y modulus]
  (let [gl (create-context canvas-id)
        program (create-shader-program gl vs-src (fs-src top-left-x top-left-y bottom-right-x bottom-right-y modulus))
        buffer (create-buffer gl)]
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
