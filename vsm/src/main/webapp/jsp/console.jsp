<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>VSM console at <%= request.getServerName() %>:<%= request.getServerPort() %></title>
<style type="text/css">
hr {
	width: 100%;
	text-align: left;
	margin-left: 0;
}
th {
    text-align: left;
}
td {
    text-align: left;
}
</style>
</head>

<body>

	<h1>VSM console at <%= request.getServerName() %>:<%= request.getServerPort() %></h1>

	<h2>Status</h2>
	<hr>
	<table>
		<c:forEach items="${checks}" var="checkEntry">
			<tr>
				<td><b><c:out value="${checkEntry.key}" /></b></td>
				<td><c:out value="${checkEntry.value}" /></td>
			</tr>
		</c:forEach>
	</table>

	<h2>Configuration</h2>
	<hr>
	<table>
		<c:forEach items="${config}" var="configEntry">
			<tr>
				<td><b><c:out value="${configEntry.key}" /></b></td>
				<td><c:out value="${configEntry.value}" /></td>
			</tr>
		</c:forEach>
	</table>

	<c:if test="${not empty extended}">
		<h2>Monitored hypervisors (<c:out value="${fn:length(pms)}" />)</h2>
		<hr>
		<table>
			<th>Address</th>
			<th>Type</th>
			<th>Cached VMs</th>
			<c:forEach items="${pms}" var="pm">
				<tr>
					<td><c:out value="${pm.address}" /></td>
					<td><c:out value="${pm.type}" /></td>
					<td>
					   <c:forEach items="${pm.virtualMachines.cache}" var="cached" varStatus="status">
					       <c:out value="${cached}" /><c:if test="${not status.last}">, </c:if>
					   </c:forEach>
					</td>
				</tr>
			</c:forEach>
		</table>

		<h2>Monitored virtual machines (<c:out value="${fn:length(vms)}" />)</h2>
		<hr>
		<table>
			<th>Virtual machine</th>
			<th>Hypervisor</th>
			<th>Last known state</th>
			<c:forEach items="${vms}" var="vm">
				<tr>
					<td><c:out value="${vm.name}" /></td>
					<td><c:out value="${vm.physicalMachine.address}" /></td>
					<td><c:out value="${vm.lastKnownState}" /></td>
				</tr>
			</c:forEach>
		</table>
		
		<h2>Duplicated virtual machines (<c:out value="${fn:length(duplicates)}" />)</h2>
		<hr>
		<table>
            <th>Virtual machine</th>
            <th>Physical machines having it</th>
            <c:forEach items="${duplicates}" var="duplicate">
                <tr>
	                <td><c:out value="${duplicate.key}" /></td>
	                <td>
	                    <c:forEach items="${duplicate.value}" var="pmdup" varStatus="status">
	                        <c:out value="${pmdup}" /><c:if test="${not status.last}">, </c:if>
	                    </c:forEach>
	                </td>
                </tr>
            </c:forEach>
		</table>
	</c:if>

</body>

</html>
