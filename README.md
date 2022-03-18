# jdg8-security

## Run the example
- Download Jboss Data Grid 8.3 from https://access.redhat.com/jbossnetwork/restricted/softwareDownload.html?softwareId=104135
- Unzip downloaded zip file
- Execute
    ``` shell
     ${jboss.datagrid.home}/bin/cli.sh user create myuser -p changeme -g admin
     ${jboss.datagrid.home}/bin/cli.sh user create admin -p changeme
    ```

- Edit ${jboss.datagrid.home}/server/conf/infinispan.xml
    ```xml
    <infinispan
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="urn:infinispan:config:12.1 https://infinispan.org/schemas/infinispan-config-12.1.xsd
                                urn:infinispan:server:12.1 https://infinispan.org/schemas/infinispan-server-12.1.xsd"
            xmlns="urn:infinispan:config:12.1"
            xmlns:server="urn:infinispan:server:12.1">
    
       <cache-container name="default" statistics="true">
          <transport cluster="${infinispan.cluster.name:cluster}" stack="${infinispan.cluster.stack:tcp}" node-name="${infinispan.node.name:}"/>
          <security>
             <authorization/>
          </security>
         <distributed-cache name="mycache" statistics="true" />
       </cache-container>
    
       <server xmlns="urn:infinispan:server:12.1">
          <interfaces>
             <interface name="public">
                <inet-address value="${infinispan.bind.address:127.0.0.1}"/>
             </interface>
          </interfaces>
    
          <socket-bindings default-interface="public" port-offset="${infinispan.socket.binding.port-offset:0}">
             <socket-binding name="default" port="${infinispan.bind.port:11222}"/>
             <socket-binding name="memcached" port="11221"/>
          </socket-bindings>
    
          <security>
             <credential-stores>
                <credential-store name="credentials" path="credentials.pfx">
                   <clear-text-credential clear-text="secret"/>
                </credential-store>
             </credential-stores>
             <security-realms>
                <security-realm name="default">
                   <properties-realm groups-attribute="Roles">
                      <user-properties path="users.properties"/>
                      <group-properties path="groups.properties"/>
                   </properties-realm>
                </security-realm>
             </security-realms>
          </security>
    
          <endpoints socket-binding="default" security-realm="default">
            <hotrod-connector name="hotrod" cache-container="default" />
             <rest-connector name="rest">
                <authentication mechanisms="DIGEST-SHA-256"/>
            </rest-connector>
           </endpoints>
       </server>
    </infinispan>
    ```

- Start the server
    ```shell
    ${jboss.datagrid.home}/bin/server.sh
    ```

- Run the main class com.redhat.example.RemoteCacheExample

- Verify the output
  ```shell
  [Before put] Statistics currentNumberOfEntries: 0
  [After put] Statistics currentNumberOfEntries: 1
  Value of key 'firstKey' retrieved from remote cache: firstValue
  [After remove] Statistics currentNumberOfEntries: 0
  ```