# Automatic To Smartthings
This is a fullstack package to have the **Automatic OBD Adapter** integrated with **Smartthings**

* Immediately push every Automatic event update to Smartthings
* Provide a new device type on Smartthings as a Presence Sensor
* Display distance, fuel level, battery voltage, MPG and last know parking location
* Directly integrated, no other resources needed

### What do you need?

* A Smartthings Developer's account
* An AWS (Amazon Web Serivce) account
* An Automatic Developer's account

### Instruction

#### Smartthings
* Create a new **Device Handler** in Smartthings Developer's [website](https://graph.api.smartthings.com/ide/devices), with the code from **DeviceHandler.groovy**
* Create a new device from [device list](https://graph.api.smartthings.com/device/create), select type **Automatic**.
* Create a new Web Service Smart App at [Here](https://graph.api.smartthings.com/ide/app/create) with code from **SmartApp.groovy**, and follow the Smartthings document to create an **SmartApp API Endpoint** and **SmartApp API Token**.

#### AWS Lambda
* Create an AWS Lambda function, with the code from **/AWS_Lambda/index.js**, Also remember to setup environment variable **ss_apiuri** and **ss_token** in Lambda with the value from above step. Configure the function to be triggered by AWS API Gateway, and make a public access and copy the **AWS API Endpoint**.
* Since AWS Lambda does not support npm install a proper node_module folder has to be generated from a local environment, and zip everything up to Lambda.

#### Automatic
* Create an App in Automatic developer's [website](https://developer.automatic.com/), setup webhook in Event Delivery part with **AWS API Endpoint** as Webhook URL. Use "Connect with Automatic Button" option on the same page to connect your Automatic Account with your Automatic App.



### How everything works
* Automatic Dev App (POST) -> AWS API Gateway -> AWS Lambda (POST) -> Smartthings Smart App -> Smartthings Device Handler
