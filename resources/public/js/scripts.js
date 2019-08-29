//------------------- MISCELLANEOUS SCRIPTS -------------------//

//Open/close controls on mobile
function ToggleControls()
{
    if ($('.control').css('bottom')=='0px')
    {
        $('.control').css('bottom','');
        $('#ctr-show').html('Show Controls');
        if ($changeMade)
        {
            setTimeout(graph,710);
        }
    }
    else
    {
        $('.control').css('bottom','0px');
        $('#ctr-show').html('Hide Controls');
    }
}

//Text in input fields changed
$changeMade = false;
function TextChange()
{
    $("#graphbutton").css({"visibility":"visible","opacity":"1"});
    $changeMade = true;
}

//Smooth scrolling
$(document).ready(function() { $("a.smooth").click(function(e)
{
    e.preventDefault();
    $("html,body").animate({scrollTop:$($.attr(this,"href")).offset().top},1000);
});});

//Show graph label on mouseover
$(document).ready(function() { $(".graph").mousemove(function(e) {
    var x = e.pageX - $(this).offset().left;
    var y = e.pageY - $(this).offset().top;
    var tlc = new Complex(centre.Real()-nmPerPx*canvas.width/2, centre.Imaginary()+nmPerPx*canvas.height/2);
    var brc = new Complex(centre.Real()+nmPerPx*canvas.width/2, centre.Imaginary()-nmPerPx*canvas.height/2);
    var z = new Complex(tlc.Real()+x*nmPerPx,tlc.Imaginary()-y*nmPerPx);
    $("#graphlbl").html(z.ToString());
});});
$(document).ready(function() { $(".graph").mouseenter(function(e) {
    $("#graphlbl").css("visibility", "visible")
});});
$(document).ready(function() { $(".graph").mouseleave(function(e) {
    $("#graphlbl").css("visibility", "")
});});
