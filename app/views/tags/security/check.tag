#{if controllers.Security.connected() && controllers.Security.check(_arg)}
    #{doBody /}
#{/if}
