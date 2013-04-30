<%--

    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License.  You may obtain a
    copy of the License at the following location:

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<jsp:directive.include file="../../default/ui/includes/top.jsp" />
<div id="welcome">
	<h2>
		<spring:message code="screen.oauth.confirm.header" />
	</h2>
	
    <!-- Authorization request = ${authorizationRequest} -->

	<p>
		<spring:message code="screen.oauth.confirm.message"
			arguments="${client_id}" />
	</p>
	<!--
	   the client id is ${client_id} and the authorizePath is ${authorizePath} 
	 -->
	<form id='confirmationForm' name='confirmationForm'
		action='authorize' method='post'>
		<input name='user_oauth_approval' value='true' type='hidden' /> <label><input
			name='authorize' value='Authorize' type='submit'></label>
	</form>
	<form id='denialForm' name='denialForm'
		action='authorize' method='post'>
		<input name='user_oauth_approval' value='false' type='hidden' /> <label>
			<input name='deny' value='Deny' type='submit'>
		</label>
	</form>
</div>
<jsp:directive.include file="../../default/ui/includes/bottom.jsp" />
