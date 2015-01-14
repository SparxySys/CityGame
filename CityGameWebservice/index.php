<?php
// Include the Slim framework
require 'Slim/Slim.php';

\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();

// MySQL database access:
require_once 'Config/config.php';
require_once 'Database/gamecontent.db.php';
$database = new mysqli($dbhost, $dbuser, $dbpassword, $dbname);

// Objects used
require_once 'Database/gamecontent.class.php';

// Application routing:
// Game content data -- GET
$app->get(
    '/gamecontent/:id',
    function ($id) use ($database)
	{
        $gamecontentdb = new GameContentDb($database);
		$content = $gamecontentdb->getGameContentById($id);
		echo json_encode($content);
    }
);

// USER CRUD
// GET route
$app->get(
    '/user/',
    function () 
	{
        echo 'This is a GET route';
    }
);

// POST route -- Create
$app->post(
    '/user/post',
    function () 
	{
        echo 'This is a POST route';
    }
);

// PATCH route -- Update
$app->patch('/user/patch',
	function () 
	{
		echo 'This is a PATCH route';
	}
);

// DELETE route -- Delete
$app->delete(
    '/user/delete',
    function () 
	{
        echo 'This is a DELETE route';
    }
);

// Run the Slim Framework application
$app->run();