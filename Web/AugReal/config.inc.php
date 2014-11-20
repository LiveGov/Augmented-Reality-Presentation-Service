<?php

   // - Setup -------
   const _admin_email = 'myemail@mycompany.com';
   const _site_name   = 'myAugRealsite';

  /* Pre-define connection to the MySQL database, please specify these fields based on your database configuration.*/
  const _mysql_username = 'root';
  const _mysql_password = 'mypassword';

  /* Pre-define connection to the remote Service Center (authentication, billing etc.) */
  const _sc_username  = 'ar_api_user';
  const _sc_password  = 'myremotepassword';
  const _sc_url       = "https://urbanplanning.yucat.com/servicecenter/api/";
  const _sc_url_diag= "https://testservicecenter.yucat.com/servicecenter/api/diagnostics/";
  const _sc_customerid= 2; // Urban planning



//-------------------------------------------  
  define('SITENAME',   _site_name);
  define('ADMINEMAIL', _admin_email);
  define('DBHOST', 'localhost');
  define('DBDATA', 'AugReal');
  define('DBUSER', _mysql_username);
  define('DBPASS', _mysql_password);
  define('SCUSER', _sc_username);
  define('SCPASS', _sc_password);
  define('SCURL', _sc_url);
  define('SCURLDIAG', _sc_url_diag);
  define('SCCUSTOMERID', _sc_customerid);
?>
