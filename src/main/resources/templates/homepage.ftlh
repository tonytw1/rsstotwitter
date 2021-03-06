<h2>RSS to Twitter</h2>

<#if account??>
    ${account.username}
    <a href="/signout">Sign out</a>
<#else>
    <a href="oauth/login">Sign in</a>
</#if>

<#if account??>
    <h4>Feeds</h4>
    <p>These feeds will be tweeted to your Twitter account ${account.username}.</p>
    <p><a href="/new">Add new</a></p>
    <ul>
        <#list jobs as job>
            <li>
                <p><a href="/feeds/${job.objectId}">${job.feed.url}</a><p>
            </li>
        </#list>
    </ul>
</#if>
