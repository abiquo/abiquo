<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <title>VSM console at <%= request.getServerName() %>:<%= request.getServerPort() %></title>
    <style type="text/css">
        th {
            border-top: 1px solid;
            border-bottom: 1px solid;
        }
        td {
            width: 300px;
        }
    </style>
</head>

<body>

    <h1>VSM console at <%= request.getServerName() %>:<%= request.getServerPort() %></h1>

    <table>
        <tr><th colspan="2">Check status</th></tr>
	    <c:forEach items="${checks}" var="checkEntry">
	        <tr>
	            <td><b><c:out value="${checkEntry.key}"/></b></td>
	            <td><c:out value="${checkEntry.value}"/></td>
	        </tr>
	    </c:forEach>
    </table>
    
    <br/>
    
    <table>
        <tr><th colspan="2">Configuration</th></tr>
        <c:forEach items="${config}" var="configEntry">
            <tr>
                <td><b><c:out value="${configEntry.key}"/></b></td>
                <td><c:out value="${configEntry.value}"/></td>
            </tr>
        </c:forEach>
    </table>
    
    <br />
    
    <c:if test="${not empty extended}">
	    <table>
	        <tr><th colspan="2">Monitored machines (<c:out value="${fn:length(pms)}"/>)</th></tr>
	        <c:forEach items="${pms}" var="pm">
	            <tr>
	                <td><b><c:out value="${pm.address}"/></b></td>
	                <td><c:out value="${pm.type}"/></td>
	            </tr>
	        </c:forEach>
	    </table>
	    
	    <br />
	    
	    <table>
	        <tr><th colspan="2">Subscriptions (<c:out value="${fn:length(vms)}"/>)</th></tr>
	        <c:forEach items="${vms}" var="vm">
	            <tr>
	                <td><b><c:out value="${vm.name}" /></b></td>
	                <td>
	                    <c:out value="${vm.physicalMachine.address}" />
	                    &nbsp;(<c:out value="${vm.physicalMachine.type}" />)
	                </td>
	            </tr>
	        </c:forEach>
	    </table>
    </c:if>
    
</body>

</html>
