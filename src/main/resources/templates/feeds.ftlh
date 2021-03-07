<#include "header.ftlh">

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

<#include "footer.ftlh">

