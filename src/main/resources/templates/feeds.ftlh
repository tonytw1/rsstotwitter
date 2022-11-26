<#include "header.ftlh">

<h4>Feeds</h4>
<p><a href="/new">Add new</a></p>
<ul>
    <#list jobs as job>
        <li>
            <p>
                <a href="/feeds/${job.job.objectId}">${job.job.feed.url}</a><br/>
                <#assign activity = job.activity>
                <#include "activity.ftlh">
            </p>
        </li>
    </#list>
</ul>

<#include "footer.ftlh">

