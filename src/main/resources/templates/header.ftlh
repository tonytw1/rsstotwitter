<html>
<body>
<h2>RSS to Twitter</h2>

<#if account??>
    ${account.username}
    <a href="/signout">Sign out</a>
<#else>
    <a href="oauth/login">Sign in</a>
</#if>
