<?php
if (stristr($_SERVER['HTTP_USER_AGENT'], 'unhackable') == false) {
	exit("Stop trying to hack the system -_-");
}

$dbuser = 'user';
$dbpass = 'pass';
$db = 'database';
$dbhost = 'localhost';

mysql_connect($dbhost, $dbuser, $dbpass) or die(mysql_error());
mysql_select_db($db) or die(mysql_error());

$n = $_POST['name'];
$c = $_POST['clan'];
$m = $_POST['cash'];
$p = $_POST['power'];

mysql_query("SELECT * FROM `stats` WHERE `Name` = '$name'");
if (mysql_affected_rows () < 1) {
	mysql_query("INSERT INTO `stats` (`Name`, `Clan`, `Cash`, `Power`) VALUES ('$n', '$c', '$m', '$p')") or die(mysql_error());
} else if (mysql_affected_rows () >= 1) {
	mysql_query("UPDATE `stats` SET `Clan`='$c', `Cash`='$m', `Power`='$p' WHERE `Name`='$n'") or die(mysql_error());
}
echo("Successfully updated stats for $n with the clan $c and cash at $m and a power level of $p!");
?>