<#include "header.ftlh">

<h4><a href="/">Feeds</a> / <a href="${job.feed.url}">${job.feed.url}</a></h4>

<h4>Feed items</h4>
<ul>
    <#list feedItemsWithTweets as feedItem>
        <li>
            <p>
                <a href="${feedItem.value0.link}" target="_blank">${feedItem.value0.title}</a><br/>
                ${(feedItem.value0.author)!}
                ${feedItem.value0.publishedDate?datetime?string("dd-MM-yyyy")}<br/>
                <ul>
                    <#list feedItem.getValue1() as tweetEvent>
                         <li>
                            <a href="$tweetEvent.tweet.getPreviewUrl()}" target="_blank">
                            ${tweetEvent.twit} </a><br/>
                            ${tweetEvent.date?datetime?string("dd-MM-yyyy")}
                            ${tweetEvent.destination}
                        </li>
                    </#list>
                </ul>
            </p>
        </li>
    </#list>
</ul>

<h4>Sent</h4>
<p><#include "activity.ftlh"></p>
<ul>
    <#list tweetEvents as tweetEvent>
        <li>
            <a href="$tweetEvent.tweet.getPreviewUrl()" target="_blank">
                ${tweetEvent.twit} </a><br/>
                ${tweetEvent.date?datetime?string("dd-MM-yyyy")}
                ${tweetEvent.destination}
        </li>
    </#list>
</ul>

<#include "footer.ftlh">
