# Vigilator node
Vigilator node is a access point to receive and send monitoring updates

The node functions as a central point for retrieving monitoring information as well as receiving monitoring information.
Vigilator apps retrieve status updates from the node that they themselfs cannot (or should not due to reasons) retrieve.

## Configuring

To configure the application edit the properties in the `src/resources` folder or add your own properties file and point the application in the direction of the folder. The application will read all files in the folder.

The reading of the monitored resources is currently fail fast, meaning that if an error with a file is detected the process will stop reading and throw a exception indicating that the application cannot start, containing the found issue

### Configuring resource files

A resourcefile is a textbased file, preferably having the `.config` extension.
The resource file is a tabbed configuration file.

Currently there are three type of resources.
1. Exposed resource
->  a monitored resource (e.g. webservice, website) available via internet
2. Internal resource 
-> a monitored resource that is monitored internally on a server. It is not available on the internet and only accesible on the server it is running on. 
It therefor will only share its updates with Vigilator via an OnboardResource.
3.  Onboard resource
-> a monitored resource that is onboard a server. It monitors the server itself as well as internal resources that might be running on the server.
The onboard resource will send information updates to the monitor at a given interval.

Example of a base resource file with one exposed resource
```
ExposedResource
	NameOfTheResource
		Config
			active: true
			url: https://localhost:48/monitor
		Web
			url: https://localhost.com
			title: Page title
		MonitoredObjectName
			items
				status: !ACTIVE
				pool size: > 50
				threads queued: > 500 W
				threads completed: == 0 W
			datetime: < 5min
		MonitoredObjectName
			items
				status: !ACTIVE
				pool size: != 1 W
				threads queued: > 10 W
				threads completed: == 0 W
			datetime: < 5min
OnboardResource
	SomeName
		Config
			active: true
InternalResource
	SomeName
```

The `ExposedResource` entry creates a new exposed resource to be monitored.

The `NameOfTheResource` can be anything, but is not allowed to contain spaces, e.g. `MyWebservice`, `my-webservice`, `myWebservice` are all valid names.

The `Config` object is mandatory for each resource and specifies whether the resource is active and allows more resource wide configurations. See [configuring the resource](# Configuring the resource)

The `Web` object can be attached to an exposed resource to enable monitoring the availability of a webpage. It requires the URL the webpage can be accessed on and optionally a title which is used to find in the page. 
If the title cannot be found in the retrieved data (webpage), the resource is considered to be unhealthy.

The `MonitoredObjectName` can be any name. It is used to correlate with an object in the monitored data received. 
It can contain items, each item is a condition that is validated against the value of the given key in the monitored data.
The datetime object specifies the maximum age of the recieved data for the object, if the data exceeds the timelimit, the object is considered unhealthy.

### Conditions

Conditions specify when a resource is *unhealthy*, so configure the resources with conditions that represent an error / or warning state and not a healty state.
Simple example, if you monitor that a database connection is available then the condition is configured to fail when it is not available becoming: `databaseAvailable: == false` or `databaseAvailable: !true`.

The following condition types are available to configure conditions for a resource:
- `==`, the value must be equal to | example: `== running` / `== 0`. The condition can be specified with or without a space after the sign, both `==value` and `== value` are valid conditions.
- `!`, the value must not be equal to | example: `!ACTIVE` / `!10`. The sign is allowed to occur within the condition, resulting in a partial not equal validation for example: `pending transactions !0`.
- `>`, the value is bigger then | example: `> 50`.
- `<`, the value is less then | example: `< 40`.

#### Types

Every number in a condition can be annotated with a `%`, making the condition validate a percentage.

Some conditions can be combined with a temporal type.
The following temporal types are available:
- `min`, specifies the value is of type minutes and can be used with the `>` and `<` signs | example: `< 5min` indicates the value must be less then 5 min old.
- ... more coming

Temportal types are mainly used to validate if the received data is recent.

#### Priority

When a condition is not urgent, the `W` can be added as the last character in the condition.
This will make the condition result in a warning instead of a error. 

For example: `threadsQueued: > 10 W` will result in a warning when the amount is above 10.

### Configuring the resource

The config object contains resource wide configurations.
Currently the following general configurations can be set:
- `active` = either `true|false`, if not active the resource will not be monitored

And the following exposed resource specific configuration can be set:
- `url` = a valid url pointing to the monitor endpoint of the application to monitor 
- `ignoreTLSIssues` = either `true|false`, if true any http connections to the monitor endpoint of the resource will infer TLS issues as warning instead of error.

## Test vigilator node

Unit and integration tests are available in the `src/test` folder.