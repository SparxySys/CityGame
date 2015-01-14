<?php
class DBLayer
{
	var $link_id;
	var $query_result;
	var $saved_queries = array();
	var $num_queries = 0;

	function DBLayer($db_host, $db_name, $db_username, $db_password)
	{
		$this->link_id = @mysqli_connect($db_host, $db_username, $db_password, true);

		if ($this->link_id)
		{
			if (@mysqli_select_db($db_name, $this->link_id))
			return $this->link_id;
			else
			$this->error('Unable to select database. MySQL reported: '.mysqli_error(), __FILE__, __LINE__);
		}
		else
		$this->error('Unable to connect to MySQL server. MySQL reported: '.mysqli_error(), __FILE__, __LINE__);
	}

	function query($sql)
	{
		$this->query_result = @mysqli_query($sql, $this->link_id);

		if ($this->query_result)
		{
			++$this->num_queries;
			return $this->query_result;
		}
		else
		{
			return false;
		}
	}


	function result($query_id = 0, $row = 0)
	{
		return ($query_id) ? @mysqli_result($query_id, $row) : false;
	}


	function fetch_assoc($query_id = 0)
	{
		return ($query_id) ? @mysqli_fetch_assoc($query_id) : false;
	}


	function fetch_row($query_id = 0)
	{
		return ($query_id) ? @mysqli_fetch_row($query_id) : false;
	}


	function num_rows($query_id = 0)
	{
		return ($query_id) ? @mysqli_num_rows($query_id) : false;
	}


	function affected_rows()
	{
		return ($this->link_id) ? @mysqli_affected_rows($this->link_id) : false;
	}


	function insert_id()
	{
		return ($this->link_id) ? @mysqli_insert_id($this->link_id) : false;
	}


	function get_num_queries()
	{
		return $this->num_queries;
	}


	function get_saved_queries()
	{
		return $this->saved_queries;
	}


	function free_result($query_id = false)
	{
		return ($query_id) ? @mysqli_free_result($query_id) : false;
	}


	function escape($str)
	{
		if (function_exists('mysqli_real_escape_string'))
		return mysqli_real_escape_string($str, $this->link_id);
		else
		return mysqli_escape_string($str);
	}


	function error()
	{
		$result['error_sql'] = @current(@end($this->saved_queries));
		$result['error_no'] = @mysqli_errno($this->link_id);
		$result['error_msg'] = @mysqli_error($this->link_id);

		return $result;
	}


	function close()
	{
		if ($this->link_id)
		{
			if ($this->query_result)
			@mysqli_free_result($this->query_result);

			return @mysqli_close($this->link_id);
		}
		else
		return false;
	}
}
?>