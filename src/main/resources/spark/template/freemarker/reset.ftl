<#assign content>
<div id = "top-banner">
	<h1 id = "banner">Deliveries @ Brown</h1>
	<ul>
		<li><a href="/about">About D@B</a></li>
		<li><a href="/faq">FAQs</a></li>
	</ul>
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
    <input type="text" class="form-control" id="1" placeholder="1">
    <input type="text" class="form-control" id="2" placeholder="2">
    <input type="text" class="form-control" id="3" placeholder="3">
    <input type="text" class="form-control" id="4" placeholder="4">
    <input type="text" class="form-control" id="5" placeholder="5">
    <input type="text" class="form-control" id="6" placeholder="6">
  </div>
  <type="submit" button onclick = "validate()" class="btn btn-default">Validate Code</button>
</form>
</div>
<div id = "enter_password">
<form class="form-horizontal">
  <div class="form-group">
    <label class="sr-only" for="exampleInputEmail3">Enter new password</label>
    <input type="password" class="form-control" id="password" placeholder="Password">
    <input type = "password" class = "form-control" id = "password_check" placeholder = "Re-enter passward">
  </div>
  <type="submit" button onclick = "reset()" class="btn btn-default">Send Code</button>
</form>
</div>
</#assign>
<#include "main.ftl">
<script src = "/js/reset.js"></script>
<link rel="stylesheet" type="text/css" href="css/reset.css">