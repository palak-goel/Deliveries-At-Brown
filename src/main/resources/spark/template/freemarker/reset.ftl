<#assign content>
<div id = "top-banner">
	<h1 id = "banner">Deliveries @ Brown</h1>
</div>
<div id = "reset">
<p>Enter Phone Number To Reset Password</p>
<form class="form-inline">
  <div class="form-group">
    <label class="sr-only" for="exampleInputEmail3">Phone Number</label>
    <input type="text" class="form-control" id="phone" placeholder="Phone">
  </div>
  <type="submit" button onclick = "reset()" class="btn btn-default">Send Code</button>
</form>
</div>
<div id = "loader"></div>
<div id = "enter_code">
<p>A code has been sent to your phone. Enter it here.</p>
<form class="form-inline">
  <div class="form-group">
    <input type="text" class="form-control" id="b1" placeholder="1" maxlength="1" size="1">
    <input type="text" class="form-control" id="b2" placeholder="2" maxlength="1" size="1">
    <input type="text" class="form-control" id="b3" placeholder="3" maxlength="1" size="1">
    <input type="text" class="form-control" id="b4" placeholder="4" maxlength="1" size="1">
    <input type="text" class="form-control" id="b5" placeholder="5" maxlength="1" size="1">
    <input type="text" class="form-control" id="b6" placeholder="6" maxlength="1" size="1">
  </div>
  <type="submit" button onclick = "validate()" class="btn btn-default">Validate Code</button>
</form>
</div>
<div id = "enter_password">
<form class="form-horizontal">
  <div class="form-group">
      <label class="sr-only" for="exampleInputEmail3">Enter account email</label>
  	<input type ="email" class = "form-control" id = "account_email" placeholder = "Email">
    <label class="sr-only" for="exampleInputEmail3">Enter new password</label>
    <input type="password" class="form-control" id="password" placeholder="Password">
    <input type = "password" class = "form-control" id = "password_check" placeholder = "Re-enter passward">
  </div>
  <type="submit" button onclick = "update()" class="btn btn-default">Update password</button>
</form>
</div>
<div id = "success">
<p>Your password has been successfully reset</p>
<form class = "form-inline" action = "/login">
	<input type="button" onclick="location.href='/login';" class = "btn btn-default" value="Back to login"/>
</form>
</div>
</#assign>
<#include "main.ftl">
<script src = "/js/reset.js"></script>
<link rel="stylesheet" type="text/css" href="css/reset.css">