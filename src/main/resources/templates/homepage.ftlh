<#include "header.ftlh">

<#list destinations as destination>
    <p>
        <a href="${destination.signin}">
            <img src="${destination.logo}" width="72" height="72"> Sign in using ${destination.displayName}
        </a>
    </p>
</#list>

<#include "footer.ftlh">
