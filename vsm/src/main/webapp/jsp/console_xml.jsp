<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%><?xml version="1.0" encoding="UTF-8"?>
<vsm host="<%= request.getServerName() %>" port="<%= request.getServerPort() %>">
    <status>				
        <check><c:out value="${checks['VSM check result']}"/></check>
        <redis><c:out value="${checks['Redis listening']}"/></redis>
        <rabbitmq><c:out value="${checks['RabbitMQ listening']}"/></rabbitmq>
    </status>
    <configuration>
        <redis>
            <host><c:out value="${config['Redis host']}"/></host>
            <port><c:out value="${config['Redis port']}"/></port>
        </redis>
        <rabbitmq>
            <host><c:out value="${config['RabbitMQ host']}"/></host>
            <port><c:out value="${config['RabbitMQ port']}"/></port>
        </rabbitmq>
    </configuration>
    <c:if test="${not empty extended}"><hypervisors><c:forEach items="${pms}" var="pm">
        <hypervisor>
            <address><c:out value="${pm.address}" /></address>
            <type><c:out value="${pm.type}" /></type>
            <cache><c:forEach items="${pm.virtualMachines.cache}" var="cached"><entry><c:out value="${cached}" /></entry>
            </c:forEach></cache>
        </hypervisor></c:forEach>
    </hypervisors>
    <virtualmachines><c:forEach items="${vms}" var="vm">
        <virtualmachine>
            <name><c:out value="${vm.name}" /></name>
            <hypervisor><c:out value="${vm.physicalMachine.address}" /></hypervisor>
            <lastevent><c:out value="${vm.lastKnownState}" /><lastevent>
        </virtualmachine></c:forEach>
    </virtualmachines>
    <duplicates><c:forEach items="${duplicates}" var="duplicate">
        <duplicate>
            <virtualmachine><c:out value="${duplicate.key}" /></virtualmachine>
            <hypervisors><c:forEach items="${duplicate.value}" var="pmdup">
                <hypervisor><c:out value="${pmdup}" /></hypervisor></c:forEach>
            </hypervisors>
        </duplicate></c:forEach>
    </duplicates></c:if>
</vsm>
