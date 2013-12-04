<?php
foreach (scandir(dirname(__FILE__)) as $filename) {
    $path = dirname(__FILE__) . '/' . $filename;
    if (is_file($path) === TRUE && $filename !== "all.php") {
        require_once $path;
    }
	else if ($filename !== "." && $filename !== ".." && $filename !== "all.php"){
		require_once $path."/"."all.php";
	}
}
?>