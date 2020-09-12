(ns complex-grapher.ui.about)

(defn about []
  [:div {:class "about-wrapper" :id "about"}
   [:div {:class "about"}
    [:h1 "About the Complex Grapher"]
    [:div {:id "intro"}
     [:h2 "What is the Complex Grapher?"]
     [:p "The Complex Grapher creates visualizations of complex functions
         (i.e. functions of a complex variable). If you are unfamiliar with
         complex numbers and functions, you can "
         [:a {:href "#expl"} "click here"]
         " to learn more about them."]
     [:p "This program uses the " [:i "domain coloring"] " method to graph
         complex functions. The graph is a representation of the complex plane:
         each pixel corresponds to a complex number $z$, with the pixel's
         horizontal and vertical position representing the real and imaginary
         components of $z$, respectively. The color of the pixel is determined
         by the value of $f(z)$, where $f$ is the function that is being graphed. "
         [:a {:href "#domain"} "Click here"] " to read more
         about domain coloring."]]
    [:div {:id "help"}
     [:h2 "Using the Complex Grapher"]
     [:p "You can enter a mathematical expression in the \"Function\" textbox.
         The expression must use $z$ as the input variable. The imaginary unit
         $i$ is also valid, as well as the constants $\\pi$ and $e$, which can
         be expressed as \"pi\" and \"e\" in the textbox. The Complex Grapher
         currently supports the following mathematical operations: addition
         (+), subtraction (-), multiplication (*), division (/), and
         exponentiation (^).  Additionally, the following built-in functions
         are supported: sine (sin), cosine (cos), tangent (tan), the natural
         logarithm (log), the real part function (re), the imaginary part
         function (im), the argument function (arg), and the magintude function
         (mag)."]
     [:p "The \"Magnitude modulus\" textbox must contain a number greater than
         0. To find out what this value does, please read the section on "
         [:a {:href "#domain"} "domain coloring."]]
     [:p "The \"Top left corner\" label specifies the complex number which is
         represented by the top-left pixel on the graph. The \"Bottom right
         corner\" label does the same for the opposite corner."]
     [:p "The arrow buttons shift the graph along the complex plane, while the
         plus/minus buttons zoom the graph in and out."]]
    [:div {:id "domain"}
     [:h2 "Domain Coloring: How it Works"]
     [:p "In domain coloring, each pixel on the graph corresponds to a complex
         number $z$. The pixel's horizontal and vertical positions represent
         the real and imaginary components of $z$, respectively. The value of
         $f(z)$ is then calculated, and the pixel's color is based on this
         value."]
     [:p "The color does not depend on the real/imaginary components of $f(z)$:
         the argument and magnitude of $f(z)$ are used instead. The argument
         determines the hue, while the magnitude determines the brightness."]
     [:p "We come across an issue here: the pixel's brightness has a maximum,
         while the magnitude of $f(z)$ does not. So, the magnitude is computed
         modulo some number $n$ in order to keep it within a certain range. The
         \"Modulo magnitude\" setting controls the value of this number $n$. If
         $m$ is the magnitude of $f(z)$, then the exact formula for calculating
         the brightness is:"]
     [:p {:class "bigmath"}
         "$$
         b =
         \\begin{cases}
             (m\\% n)/n, & \\mbox{if } (m\\% 2n) \\leq n \\\\
             1-(m\\% n)/n, & \\mbox{if } (m\\% 2n) > n
         \\end{cases}$$"]
     [:p "where $b$ is the brightness, in the range 0 to 1 inclusive."]]
    [:div {:id "expl"}
     [:h2 "An Introduction to Complex Numbers"]
     [:p [:i "The following is a brief explanation of complex numbers for those
             who are unfamiliar with the concept."]]
     [:p "You've probably been taught in high school that you can't calculate
         the square root of a negative number. It's certainly true that the
         result won't be a " [:i "real number"]", but what if we create an
         entirely new set of numbers to account for square roots of negatives?
         Let's start by creating a number which we will call $i$:"]
     [:p "$$i = \\sqrt{-1}$$"]
     [:p "The square root of any negative number can now be written as a
         multiple of $i$. For example:"]
     [:p "$$\\sqrt{-9}$$
          $$=\\sqrt{9}\\sqrt{-1}$$
          $$=3i$$"]
     [:p "Numbers that are a multiple of $i$ are not real numbers: hence, they
         are named " [:i "imaginary numbers"] ". $3i$, $5i$, $6.24i$, and $\\pi i$
         are all examples of imaginary numbers. The constant $i$ is known as
         the " [:i "imaginary unit."]]
     [:p "But what happens if we add a real number and an imaginary number
         together? The result is called a " [:i "complex number"] ". Any number of
         the form $a+bi$, where $a$ and $b$ are real numbers, is a complex
         number. $a$ is called the " [:i "real part"] ", while $b$ is called the "
         [:i "imaginary part"] ". Note that $a$ and/or $b$ can be equal to 0, and
         therefore all real numbers and all imaginary numbers are also complex
         numbers."]
     [:p "Arithmetic with complex numbers is very similar to arithmetic with
         real numbers. Basic rules of arithmetic still apply. For example,
         let's simplify the expression $(3i+1)(5i-7)$:"]
     [:p {:class "bigmath"} "$$(3i+1)(5i-7)$$
                             $$=(3i)(5i)-(3i)(7)+(1)(5i)-(1)(7)$$
                             $$=15i^2-21i+5i-7$$
                             $$=-15-16i-7$$
                             $$=-16i-22$$"]
     [:p "How can we visualize complex numbers? While real numbers are
         visualized as part of a number line, complex numbers are visualized as
         part of the " [:i "complex plane"] ": this is simply a Cartesian plane, with
         the $x$-axis representing the real part of a complex number, and the
         $y$-axis representing the imaginary part. For example, the number $3+2i$
         is drawn on the complex plane below:"]
     [:div {:class "svg-wrapper"} [:img {:src "32i.svg"}]]
     [:p "There is an alternative way to represent complex numbers. Instead of
         expressing a complex number in terms of real and imaginary parts, it
         can be expressed in terms of " [:i "argument"] " and " [:i "magnitude."]
         " Consider the number $z=a+bi$, plotted on the complex plane below:"]
     [:div {:class "svg-wrapper"} [:img {:src "argmag.svg"}]]
     [:p "The angle $\\theta$ between the arrow and the $x$-axis is called the
         " [:i "argument"] " of $z$. The length of the arrow, $m$, is called the
         " [:i "magnitude"] " of $z$. With some basic trigonometry, and the
         Pythagorean theorem, it is possible to express the argument and magnitude
         in terms of $a$ and $b$:"]
     [:p "$$\\theta=\\text{tan}^{-1}(b/a)$$
          $$m=\\sqrt{a^2+b^2}$$"]
     [:p "To learn about complex numbers in more detail, visit the "
         [:a {:href "https://en.wikipedia.org/wiki/Complex_number" :target "_blank"}
          "Wikipedia page"]
         " on the topic."]]]])
