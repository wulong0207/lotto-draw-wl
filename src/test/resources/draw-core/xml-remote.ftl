	
	<!-- ${name} -->
	<bean name="/draw${className}Service" class="com.hhly.drawcore.remote.exporter.DrawHessianServiceExporter">
		<property name="service" ref="draw${className}Service" />
		<property name="serviceInterface" value="com.hhly.drawcore.remote.${package}.service.IDraw${className}Service" />
	</bean>