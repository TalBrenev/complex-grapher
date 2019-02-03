//Complex number object prototype
function Complex(real, imaginary)
{
    //Real part and imaginary part
    this.re = real;
    this.im = imaginary;

    //Whether or not the argument/magnitude was already calculated
    this.argCalc = false;
    this.magCalc = false;

    //Functions which return components of the complex number (both ordinary and polar form)
    this.Real = function()
    {
        return this.re;
    }
    this.Imaginary = function()
    {
        return this.im;
    }
    this.Magnitude = function()
    {
        if (!this.magCalc)
        {
            this.mag = Math.sqrt(Math.pow(this.re,2)+Math.pow(this.im,2));
            this.magCalc = true;
        }
        return this.mag;
    }
    this.Argument = function()
    {
        if (!this.argCalc)
        {
            this.arg = Math.atan2(this.im,this.re);
            this.argCalc = true;
        }
        return this.arg;
    }

    //Returns string representation of complex number
    this.ToString = function()
    {
        if (this.im < 0)
        {
            return  (+this.re.toFixed(2)) + "-" + (+Math.abs(this.im).toFixed(2)) + "i";
        }
        else
        {
            return (+this.re.toFixed(2)) + "+" + (+Math.abs(this.im).toFixed(2)) + "i";
        }
    }
}

//Complex number constants
const compE = new Complex(Math.E, 0);
const compI = new Complex(0, 1);
const comp2I = new Complex(0, 2);
const comp1 = new Complex(1, 0);
const comp2 = new Complex(2, 0);
const compPi = new Complex(Math.PI, 0);

//Basic arithmetic for complex numbers
function Add(c1, c2)
{
    return new Complex(c1.Real()+c2.Real(), c1.Imaginary()+c2.Imaginary());
}
function Subtract(c1, c2)
{
    return Add(c1, new Complex(0-c2.Real(),0-c2.Imaginary()));
}
function Multiply(c1, c2)
{
    return new Complex(c1.Real()*c2.Real()-c1.Imaginary()*c2.Imaginary(), c1.Real()*c2.Imaginary()+c1.Imaginary()*c2.Real());
}
function Divide(c1, c2)
{
    var q = Multiply(c1, new Complex(c2.Real(),0-c2.Imaginary()));
    var r = Math.pow(c2.Real(),2)+Math.pow(c2.Imaginary(),2);
    return new Complex(q.Real()/r,q.Imaginary()/r);
}

//Complex exponentiation
function Pow(c1, c2)
{
    var m = c1.Magnitude();
    var x = c1.Argument();
    var a = c2.Real();
    var b = c2.Imaginary();
    var logm = Math.log(m);
    var mag = Math.pow(Math.E,a*logm-b*x);
    var arg = a*x + b*logm;
    return new Complex(mag*Math.cos(arg),mag*Math.sin(arg));
}

//Complex trigonometry
function Sin(z)
{
    var iz = Multiply(compI, z);
    var e_iz = Pow(compE, iz);
    return Divide(Subtract(e_iz, Divide(comp1, e_iz)), comp2I);
}
function Cos(z)
{
    var iz = Multiply(compI, z);
    var e_iz = Pow(compE, iz);
    return Divide(Add(e_iz, Divide(comp1, e_iz)), comp2);
}
function Tan(z)
{
    var iz = Multiply(compI, z);
    var e_iz = Pow(compE, iz);
    var rec_e_iz = Divide(comp1, e_iz);
    return Divide(Subtract(e_iz, rec_e_iz), Multiply(compI, Add(e_iz, rec_e_iz)));
}

//Complex logarithm
function Log(z)
{
    return new Complex(Math.log(z.Magnitude()), z.Argument());
}
