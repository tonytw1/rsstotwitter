<#include "header.ftlh">

<h4><a href="/">Feeds</a> / ${job.objectId}</h4>

<p><a href="${job.feed.url}">${job.feed.url}</a> -> ${job.account.username}</p>

<h4>Tweets sent</h4>
<p>${lastHour} in the last hour / ${lastTwentyFourHours} in the last 24 hours</p>

<h4>Tweets</h4>
<ul>
    <#list tweets as tweet>
        <li>
            ${tweet.twit}<br/>
            ${tweet.date?datetime?string("dd-MM-yyyy")}
            <p>${tweet.tweet}</p>
        </li>
    </#list>
</ul>

<h4>Feed items</h4>
<ul>
    <#list feedItems as feedItem>
        <li>${feedItem}</li>
    </#list>
</ul>

<#include "footer.ftlh">
