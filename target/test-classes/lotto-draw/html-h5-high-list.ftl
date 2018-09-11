<#if childType=='k3'>
${r'<#include'} "/h5/high/common/template-high-list-k3.html"/>
<#elseif (childType=='kl12'||childType=='11x5')>
${r'<#include'} "/h5/high/common/template-high-list-endnum.html"/>
<#else>
${r'<#include'} "/h5/high/common/template-high-list-default.html"/>
</#if>