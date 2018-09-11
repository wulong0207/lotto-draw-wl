	<bean id="iDraw${className}Service" parent="hessianProxyConfig">
		<property name="serviceUrl" value="${r'$'}{draw_core_url}${r'$'}{remote_draw_${key}_service}"/>
		<property name="serviceInterface" value="com.hhly.drawcore.remote.${package}.service.IDraw${className}Service"/>
	</bean>