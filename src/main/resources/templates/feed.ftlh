<#include "header.ftlh">

<h4><a href="/">Feeds</a> / <a href="${job.feed.url}">${job.feed.url}</a></h4>

<h4>Tweets</h4>
<p><#include "activity.ftlh"></p>
<ul>
    <#list tweetEvents as tweetEvent>
        <li>
            <a href="https://twitter.com/${tweetEvent.tweet.author}/status/${tweetEvent.tweet.id?c}" target="_blank">
                ${tweetEvent.twit} </a><br/>
                ${tweetEvent.date?datetime?string("dd-MM-yyyy")}
        </li>
    </#list>
</ul>

<h4>Feed items</h4>
<ul>
    <#list feedItems as feedItem>
        <li>
            <p>
                <a href="${feedItem.link}" target="_blank">${feedItem.title}</a><br/>
                ${(feedItem.author)!}
                ${feedItem.publishedDate?datetime?string("dd-MM-yyyy")}<br/>
            </p>
        </li>
    </#list>
</ul>

<#include "footer.ftlh">
