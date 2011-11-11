<?php
$dbuser = 'user';
$dbpass = 'pass';
$db = 'database';
$dbhost = 'localhost';

mysql_connect($dbhost, $dbuser, $dbpass) or die(mysql_error());
mysql_select_db($db) or die(mysql_error());

$user = $_GET['user'];
$sql = mysql_query("SELECT * FROM stats where Name='$user'");

if (mysql_affected_rows () < 1) {
$user = "User Not Found.";
$clan = "N/A";
$cash = 0;
$power = 0;
} else {
$row = mysql_fetch_assoc($sql) or die(mysql_error());
$clan = ($row["Clan"]);
$cash = ($row["Cash"]);
$power = ($row["Power"]);
}


function imagettftextoutline(
$image,
$size,
$angle,
$x,
$y,
$color,
$fontfile,
$text,
$outlinewidth = 1, // 1px outline
$outlinecolor = 0 // black
) {
// First offset diagonally
imagettftext($image, $size, $angle, $x - $outlinewidth,
$y - $outlinewidth, $outlinecolor, $fontfile, $text);
imagettftext($image, $size, $angle, $x - $outlinewidth,
$y + $outlinewidth, $outlinecolor, $fontfile, $text);
imagettftext($image, $size, $angle, $x + $outlinewidth,
$y - $outlinewidth, $outlinecolor, $fontfile, $text);
imagettftext($image, $size, $angle, $x + $outlinewidth,
$y + $outlinewidth, $outlinecolor, $fontfile, $text);
// Then offset orthogonally
imagettftext($image, $size, $angle, $x - $outlinewidth, $y,
$outlinecolor, $fontfile, $text);
imagettftext($image, $size, $angle, $x + $outlinewidth, $y,
$outlinecolor, $fontfile, $text);
imagettftext($image, $size, $angle, $x, $y - $outlinewidth,
$outlinecolor, $fontfile, $text);
imagettftext($image, $size, $angle, $x, $y + $outlinewidth,
$outlinecolor, $fontfile, $text);
// Output text
imagettftext($image, $size, $angle, $x, $y, $color, $fontfile,
$text);
}


// load the image from the file specified:
$im = imagecreatefrompng("StatSig.png");
// if there's an error, stop processing the page:
if (!$im) {
die("");
}

// define some colours to use with the image
imageSaveAlpha($im, true);
$red = imagecolorallocate($im, 225, 0, 0);
$trans = imagecolorallocatealpha($im, 0, 0, 0, 127);
imagefill($im, 0, 0, $trans);

// get the width and the height of the image
$width = imagesx($im);
$height = imagesy($im);

// now we want to write in the centre of the rectangle:
$font = 'font.ttf'; // store the int ID of the system font we're using in $font
$fontsize = 14;
$fontsize2 = 45;

// finally, write the string:
imagettftextoutline($im, $fontsize2, 0, 150, 45, $red, $font, $user);

imagettftextoutline($im, $fontsize, 0, 228, 94, $red, $font, $clan);

imagettftextoutline($im, $fontsize, 0, 237, 225, $red, $font, number_format($cash));

imagettftextoutline($im, $fontsize, 0, 262, 137, $red, $font, number_format($power));

// output the image
// tell the browser what we're sending it
Header('Content-type: image/png');
// output the image as a png
imagepng($im);

// tidy up
imagedestroy($im);
?>