<#include "header.ftlh">

<h4><a href="/">Feeds</a> / <a href="${job.feed.url}">${job.feed.url}</a></h4>

<h4>Tweets sent</h4>
<p>${lastHour} in the last hour / ${lastTwentyFourHours} in the last 24 hours</p>
<ul>
    <#list tweets as tweet>
        <li>
            ${tweet.id}
            <a href="https://twitter.com/statuses/">
                ${tweet.twit}<br/>
                ${tweet.date?datetime?string("dd-MM-yyyy")}
                <p>${tweet.tweet}</p>
            </a>
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
