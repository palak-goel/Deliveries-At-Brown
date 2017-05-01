<#assign content>
<h1>Login</h1>
<input type = "email" id = "account_id" placeholder = "Enter email">
<input type = "password" id = "account_password" placeholder = "Enter password">
<input button onclick = "login()" type="submit" value="Login">
<h1> Or create account </h1>
<input type = "text" name = "first name" id = "first_name" placeholder = "First name">
<input type = "text" name = "last_name" id = "last_name" placeholder = "Last name"> <br>
<input type = "email" name = "new_email" id = "new_email" placeholder = "Email"> <br>
<input type = "text" name = "cell" id = "cell" placeholder = "Phone Number"> <br>
<input type = "password" name = "password" id = "password" placeholder = "Password"> <br>
<input type = "password" id = "password_check" placeholder = "Re-enter Password"> <br>
<input type = "hidden" name = "from" id = "from" value = ${from}>
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
<input id = "create_account" type = "submit" value = "Create Account">
</#assign>
<#include "main.ftl">
<script src = "/js/login.js"></script>
