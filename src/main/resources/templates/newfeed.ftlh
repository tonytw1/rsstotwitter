<#include "header.ftlh">
${account.username}
<a href="/signout">Sign out</a>

<h4>Adding a feed</h4>

<form method="POST" action="/new">
    <div class="form-group">
        <label for="url" class="col-sm-2 control-label">Feed URL</label>
        <div class="col-sm-5">
            <input id="url" name="url" placeholder="http://" value="" class="form-control" />
        </div>
    </div>
    <input type="submit">Add feed</input>
</form>

<#include "footer.ftlh">
