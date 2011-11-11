<?php
	$user = 'username';
	$pass = 'password';
	$db = 'database';
	$dbhost = 'localhost';
	
	$connection = mysql_connect($dbhost, $user, $pass);
	mysql_select_db($db, $connection);
	$query = "SELECT Name, Power, Cash FROM stats ORDER BY Power desc";
	$qid = mysql_query($query, $connection);
	$output = "
	<center>
		<h3>
			MonsterCraft Highscores
		</h3>";
		$output .= "
		<table>
				<tr>
					<th>Rank</th>
					<th>Name</th>
					<th>Power</th>
					<th>Cash</th>
				</tr>
		";
		$rank = 1;
		while (list($name, $power, $cash) = mysql_fetch_array($qid)) {		
			$output .= "
			<tr>
					<td>
						$rank
					</td>
					<td>
						$name
					</td>
					<td>
						$power
					</td>
					<td>
						$cash
					</td>
			</tr>
			";
			$rank++;
		}
		$output .= "
		</table>
	</center>
	";
mysql_close($connection);
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
	<head>
	<title>MonsterCraft Highscores</title>
	<meta http-equiv="content-type" content="text/html;charset=utf-8" />
		<style>
		table td {
			text-align: center;
			color: 	#ccc;
			width: 600px;
			font-size: 18px;
		}
		table th {
			text-align: center;
			margin: 15px;
			color: 	#0099FF;
			font-size: 24px;
			border-bottom: 2px solid #000015;
			filter:progid:DXImageTransform.Microsoft.Glow(Strength=1, #ccc, Enabled);
		}
		body td:hover {
			color: 	#ccc;
			font-size: 22px;
			width: 600px;
		}
		</style>
	</head>
	<body bgcolor="#202020">
	<?php print $output; ?>
</body>
</html>