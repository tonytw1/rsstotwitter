<h2>RSS to Twitter</h2>

<h4>Feeds / ${job.objectId}</h4>

<p>${job}</p>

<p><a href="${job.feed.url}">${job.feed.url}</a>

<p>${lastHour} / ${lastTwentyFourHours}</p>

<ul>
    <#list feedItems as feedItem>
        <li>${feedItem}</li>
    </#list>
</ul>
