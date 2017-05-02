<#assign content>
 <div id = "top-banner">
        <h1 id = "banner">Deliveries @ Brown</h1>
        <ul>
          <li><a href="/about">About D@B</a></li>
          <li><a href="/faq">FAQs</a></li>
        </ul>
    </div>
<div id = "login">
<h1>Login</h1>
<form class="form-inline">
  <div class="form-group">
    <label class="sr-only" for="exampleInputEmail3">Email address</label>
    <input type="email" class="form-control" id="account_id" placeholder="Email">
  </div>
  <div class="form-group">
    <label class="sr-only" for="exampleInputPassword3">Password</label>
    <input type="password" class="form-control" id="account_password" placeholder="Password">
  </div>
  <br>
  <a href = "/forgot-password">Forgot password?</a><br>
  <type="submit" button onclick = "login()" class="btn btn-default">Sign in</button>
</form>
</div>
<div id = "create_div">
<h1> Create Account </h1>
<form class="form-horizontal">
  <div class="form-group">
    <label class="sr-only" for="exampleInputEmail3">First name</label>
    <input type="text" class="form-control" id="first_name" placeholder="First name">
  </div>
  <div class="form-group">
    <label class="sr-only" for="exampleInputEmail3">Last name</label>
    <input type="text" class="form-control" id="last_name" placeholder="Last name">
  </div>
  <div class="form-group">
    <label class="sr-only" for="exampleInputEmail3">Email address</label>
    <input type="email" class="form-control" id="new_email" placeholder="Email">
  </div>
  <div class="form-group">
    <label class="sr-only" for="exampleInputEmail3">Cell</label>
    <input type="text" class="form-control" id="cell" placeholder="Cellphone number">
  </div>
  <div class="form-group">
    <label class="sr-only" for="exampleInputEmail3">Password</label>
    <input type="password" class="form-control" id="password" placeholder="Enter password">
  </div>
  <div class="form-group">
    <label class="sr-only" for="exampleInputEmail3">Re-enter password</label>
    <input type="text" class="form-control" id="password_check" placeholder="Re-enter password">
  </div>
  <input type = "hidden" name = "from" id = "from" value = "${from}">
  <div class="form-row">
    <label for="card-element">
      Credit or debit card
    </label>
    <div id="card-element">
      <!-- a Stripe Element will be inserted here. -->
    </div>

    <!-- Used to display form errors -->
    <div id="card-errors"></div>
  </div>
  <type="submit" id = "create_account" class="btn btn-default">Create account</button>
</form>
</div>
</#assign>
<#include "main.ftl">
<script src = "/js/login.js"></script>
<link rel="stylesheet" type="text/css" href="css/login.css">