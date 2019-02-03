//Variables storing graph location and zoom
var centre = new Complex(0,0);
var nmPerPx = 0.01;

//Initialization function
function init()
{
    //Make "main" div temporarily visible in order to set canvas size
    document.getElementById("main").style.display = "block";
    document.getElementById("canvas").height = $("canvas").height();
    document.getElementById("canvas").width = $("canvas").width();
    document.getElementById("main").style.display = "none";

    //Set up window resize event
    window.onresize = resize;
    $w = $(window).width();

    //Graph the default function
    graph();
}

//Function to be called on window resizing
function resize()
{
    //Do not do anything if the resize is only vertical, unless the width is greater than 1080
    if ($(window).width() == $w && !($w > 1080))
    {
        return;
    }
    $w = $(window).width();

    //If "main" div is not yet visible, temporarily make it visible for resize
    var tempVisible = false;
    if (document.getElementById("main").style.display == "none")
    {
        document.getElementById("main").style.display = "block";
        tempVisible = true;
    }

    //Update canvas width and height and call the graph function
    document.getElementById("canvas").height = $("canvas").height();
    document.getElementById("canvas").width = $("canvas").width();
    graph();

    //If "main" div is not yet visible, temporarily make it visible for resize
    if (tempVisible)
    {
        document.getElementById("main").style.display = "none";
    }
}

//Sets the specified pixel on the canvas to the given value
function setPixel(imageData,data,x,y,r,g,b)
{
    var ind = (y*imageData.width+x)*4;
    data[ind] = r;
    data[ind+1] = g;
    data[ind+2] = b;
    data[ind+3] = 255;
}

//Evaluates the precedence of an operator
function prec(op)
{
    switch (op)
    {
        case "+":
            return 1;
            break;
        case "-":
            return 1;
            break;
        case "*":
            return 2;
            break;
        case "/":
            return 2;
            break;
        case "^":
            return 3;
            break;
        case "sin":
            return 4;
        case "cos":
            return 4;
        case "tan":
            return 4;
        case "log":
            return 4;
        default:
            return -1;
    }
}

//Converts function entered by user from infix to postfix notation using Djikstra's shunting yard algorithm
function shuntingYard(func)
{
    //Remove spaces from expression
    func = func.replace(/ /g,"");

    //Add implied * between variables and numbers, etc
    for (var i = 0; i < func.length; i++)
    {
        if (i < func.length-1)
        {
            if (/[zepi\)]/.test(func[i]) && (/[zepi\(0-9.]/.test(func[i+1]) || ["sin", "cos", "tan", "log"].indexOf(func.slice(i+1, i+4)) >= 0))
            {
                func = func.slice(0,i+1) + "*" + func.slice(i+1,func.length);
            }
        }
        if (i > 0)
        {
            if (/[zepi\(]/.test(func[i]) && /[zepi\)0-9.]/.test(func[i-1]))
            {
                func = func.slice(0,i) + "*" + func.slice(i,func.length);
            }
        }
    }

    //Add implied 0 before negative numbers
    for (var i = 0; i < func.length; i++)
    {
        if (func[i] == "-")
        {
            if (i == 0 || func[i-1] == '(')
            {
                func = func.slice(0,i) + "0" + func.slice(i,func.length);
            }
        }
    }

    //Split tokens up into individual elements of an array
    var infix = [];
    for (var i = 0; i < func.length; i++)
    {
        //Set the current character as the token
        token = func[i];

        //If the token is an operator, parenthesis, or variable, add it to the array
        if (/[zepi+*-\/\^\(\)]/.test(token))
        {
            infix.push(token);
        }

        //Otherwise, if the token is a digit
        else if (/[0-9.]/.test(token))
        {
            //Keep adding characters as long as they are digits
            i++;
            while (/[0-9.]/.test(func[i]))
            {
                token += func[i];
                i++;
            }
            i--;

            //If the token has more than one decimal point, the expression is invalid
            if ((token.match(/[.]/g) || []).length > 1)
            {
                throw "Invalid Expression";
            }

            //Add the token to the array
            infix.push(token);
        }

        //If the token and the 3 characters that follow are a function, add it to the array and skip ahead 3 characters
        else if (["sin", "cos", "tan", "log"].indexOf(func.slice(i, i+3)) >= 0)
        {
            infix.push(func.slice(i, i+3));
            i += 2;

            //If the token following the function is not an opening bracket, the expression is invalid
            if (func[i+1] != "(")
            {
                throw "Invalid Expression";
            }
        }

        //Otherwise, the expression is invalid
        else
        {
            throw "Invalid Expression";
        }
    }

    //Create operator stack and results array
    var stack = [];
    var postfix = [];
    //Go through every token in the infix expression
    for (var i = 0; i < infix.length; i++)
    {
        //Read token
        token = infix[i];

        //If the token is a number or variable, add it to the output
        if (/[0-9.]/.test(token[0]) || /^[zepi]/.test(token))
        {
            postfix.push(token);
        }

        //Otherwise, if the token is an operator
        else if (/[+*-\/\^]/.test(token) || (["sin", "cos", "tan", "log"].indexOf(token) >= 0))
        {
            //While there is an operator at the top of the stack whose precedence is > that of the current token, or (= that of the current token and the operator on the top of the stack is not right associative)
            if (stack.length > 0)
            {
                while (prec(stack[stack.length-1]) > prec(token) || (prec(stack[stack.length-1]) == prec(token) && stack[stack.length-1] != "^"))
                {
                    //Pop a token from the stack and add it to the output
                    postfix.push(stack.pop());
                    if (stack.length == 0)
                    {
                        break;
                    }
                }
            }

            //Push the token onto the stack
            stack.push(token);
        }

        //Otherwise, if the token is a left bracket, push it onto the stack
        else if (token == "(")
        {
            stack.push(token);
        }

        //Otherwise, if the token is a right bracket
        else if (token == ")")
        {
            //While there is a token on the top of the stack that is not a left bracket
            if (stack.length > 0)
            {
                while (stack[stack.length-1] != "(")
                {
                    //Pop a token from the stack and add it to the output
                    postfix.push(stack.pop());
                    if (stack.length == 0)
                    {
                        //Unbalanced parantheses
                        throw "Invalid Expression";
                    }
                }
            }
            else
            {
                //Unbalanced parantheses
                throw "Invalid Expression";
            }

            //Pop the left bracket from the stack
            stack.pop();
        }
    }

    //While there are tokens on the stack
    while (stack.length > 0)
    {
        //If the token on the top of the stack is a left bracket, there are unbalanced parantheses
        if (stack[stack.length-1] == "(")
        {
            throw "Invalid Expression";
        }

        //Pop a token from the stack and add it to the output
        postfix.push(stack.pop());
    }

    //Go through postfix expression and replace string numbers with Complex number objects
    for (var i = 0; i < postfix.length; i++)
    {
        if (postfix[i] == "i")
        {
            postfix[i] = compI;
        }
        else if (postfix[i] == "e")
        {
            postfix[i] = compE;
        }
        else if (postfix[i] == "p")
        {
            postfix[i] = compPi;
        }
        else if (/[0-9.]/.test((postfix[i])[0]))
        {
            postfix[i] = new Complex(Number(postfix[i]),0);
        }
    }

    //Return postfix expression
    return postfix;
}

//Returns the value of the function entered in the "Function" textbox at a complex number z
function funcVal(z,func)
{
    //Go through postfix expression and replace "z" strings with the z parameter
    for (var i = 0; i < func.length; i++)
    {
        if (func[i] == "z")
        {
            func[i] = z;
        }
    }

    //Evaluate postfix expression func with value z
    stack = [];
    for (var i = 0; i < func.length; i++)
    {
        token = func[i];
        if (typeof token == "string")
        {
            if (!(["sin", "cos", "tan", "log"].indexOf(token) >= 0))
            {
                var op1 = stack.pop();
                var op2 = stack.pop();
                var res = new Complex(0,0);
                switch (token)
                {
                    case "+":
                        res = Add(op2,op1);
                        break;
                    case "-":
                        res = Subtract(op2,op1);
                        break;
                    case "*":
                        res = Multiply(op2,op1);
                        break;
                    case "/":
                        res = Divide(op2,op1);
                        break;
                    case "^":
                        res = Pow(op2,op1);
                        break;
                }
            }
            else
            {
                var op = stack.pop();
                switch (token)
                {
                    case "sin":
                        res = Sin(op);
                        break;
                    case "cos":
                        res = Cos(op);
                        break;
                    case "tan":
                        res = Tan(op);
                        break;
                    case "log":
                        res = Log(op);
                        break;
                }
            }
            stack.push(res);
        }
        else
        {
            stack.push(token);
        }
    }
    if (stack.length != 1)
    {
        throw "Invalid Expression";
    }
    return stack.pop();
}

function FromHSV(h,s,v)
{
    var c = v * s;
    var x = c * (1 - Math.abs((h / 60) % 2 - 1));
    var m = v - c;
    var r = 0;
    var g = 0;
    var b = 0;
    if (0 <= h && h < 60)
    {
        r = c;
        g = x;
        b = 0;
    }
    else if (60 <= h && h < 120)
    {
        r = x;
        g = c;
        b = 0;
    }
    else if (120 <= h && h < 180)
    {
        r = 0;
        g = c;
        b = x;
    }
    else if (180 <= h && h < 240)
    {
        r = 0;
        g = x;
        b = c;
    }
    else if (240 <= h && h < 300)
    {
        r = x;
        g = 0;
        b = c;
    }
    else if (300 <= h && h <= 360)
    {
        r = c;
        g = 0;
        b = x;
    }
    return [Math.floor((r + m) * 255), Math.floor((g + m) * 255), Math.floor((b + m) * 255)];
}

//Draws a graph of the given function on the canvas
function graph()
{
    //Make the graph overlay visible, as long as the main div as already visible
    if ($("#main").css("display") == "block")
    {
        $("#overlay").css("opacity","1");
    }
    $("#error").css("opacity","");
    $("#graphbutton").css({"visibility":"","opacity":""});
    $changeMade = false;
    setTimeout(function(){try{

    //Create canvas, context, and image data for drawing
    var canvas = document.getElementById("canvas");
    var context = canvas.getContext("2d");
    var imageData = context.getImageData(0,0,canvas.width,canvas.height);
    var data = imageData.data;

    //Calculate complex numbers represented by the top left and bottom right corners, and display them
    var tlc = new Complex(centre.Real()-nmPerPx*canvas.width/2, centre.Imaginary()+nmPerPx*canvas.height/2);
    var brc = new Complex(centre.Real()+nmPerPx*canvas.width/2, centre.Imaginary()-nmPerPx*canvas.height/2);
    document.getElementById("topleftcoords").innerHTML = tlc.ToString();
    document.getElementById("bottomrightcoords").innerHTML = brc.ToString();

    //Get modulus value
    var mod = Number(document.getElementById("modulus").value);
    if (mod <= 0)
    {
        throw "Invalid Expression";
    }

    //Get function and convert it to postfix format
    var func = shuntingYard(document.getElementById("function").value);

    //For each pixel on the graph
    for (x = 0; x < canvas.width; x++)
    {
        for (y = 0; y < canvas.height; y++)
        {
            //Find the complex number z corresponding to the pixel
            var z = new Complex(tlc.Real()+x*nmPerPx,tlc.Imaginary()-y*nmPerPx);

            //Find the value of z at the function entered by the user, and calculate its magnitude and argument
            var fz = funcVal(z,func.slice(0));
            var mag = fz.Magnitude();
            var arg = fz.Argument();

            //Calculate hsv color values of pixel
            var h = Math.floor((arg + Math.PI)/Math.PI*180);
            var s = 1;
            var v = (mag % mod) / mod;
            if (mag % (2 * mod) > mod)
            {
                v = 1 - v;
            }

            //Convert hsv to rgb color and set the pixel to that color
            rgb = FromHSV(h,s,v);
            setPixel(imageData,data,x,y,rgb[0],rgb[1],rgb[2]);
        }
    }

    //Display graph on canvas
    context.putImageData(imageData,0,0);

    //Display error overlay if the function is invalid
    }
    catch (e)
    {
        $("#error").css("opacity","1");
    }

    //Make the graph overlay invisible
    $("#overlay").css("opacity","");},100);
}

//Shift functions: translates the graph by 30% of the canvas width/height in the specified direction, and then calls the graph function
function shiftUp()
{
    var canvas = document.getElementById("canvas");
    centre = new Complex(centre.Real(),centre.Imaginary()+0.3*canvas.height*nmPerPx);
    graph();
}
function shiftDown()
{
    var canvas = document.getElementById("canvas");
    centre = new Complex(centre.Real(),centre.Imaginary()-0.3*canvas.height*nmPerPx);
    graph();
}
function shiftRight()
{
    var canvas = document.getElementById("canvas");
    centre = new Complex(centre.Real()+0.3*canvas.width*nmPerPx,centre.Imaginary());
    graph();
}
function shiftLeft()
{
    var canvas = document.getElementById("canvas");
    centre = new Complex(centre.Real()-0.3*canvas.width*nmPerPx,centre.Imaginary());
    graph();
}

//Zoom functions: increases/decreases the zoom by a factor of 2, then calls the graph function
function zoomIn()
{
    nmPerPx /= 2;
    graph();
}
function zoomOut()
{
    nmPerPx *= 2;
    graph();
}
