import React, { Component } from 'react';
import './app.scss'
import CamundaForm from "./components/CamundaForm";
import {Stomp} from '@stomp/stompjs';

/* Set these params for your environment */
// Your domain name where the java spring boot is listening
const backendUrl = "localhost:8087"
// Set this to http or https
const backendProtocol = "http"
// This is the process id used to search for user tasks
const processId = "Process_twoUserTasks";
// This is the bpmn file name. It should be <fileName>.bpmn (without the suffix)
// This is used to find form schemas without having to query Tasklist rest api
const processFileName = "TwoUserTasks";
// How often do you want to poll for tasks?
const pollingIntervalMillis = 500;
/* END param section. You shouldn't need to change anything below this line */

const welcomeFormSchema = require('./forms/welcome.form.json')

// Websockets client
const sockUrl = `ws://${backendUrl}/ws`;
let stompClient = null;

// Http Rest client
const restApi = `${backendProtocol}://${backendUrl}`;
let rest = require('rest');
let mime = require('rest/interceptor/mime');
let client = rest.wrap(mime);

let merge = (a, b) => ({...a,...b});

const welcomeFormData = {
  "firstName": "Dave",
  "lastName": "Smith",
  "officeAddress": "5721 Woods Haven Drive",
  "city": "Fredericksburg",
  "state": "VA",
  "zip": "22407",
  "phone": "",
  "userName": "dave",
  "emailAddress": "david.paroulek@mycompany.com",
  "password": "camunda"
}

const initial = {
  schema: null,
  data: welcomeFormData,
  task: null,
  tasks: [],
  screen: 'welcome',
  history: {},
  initialized: false
};

class App extends Component {
  constructor(props) {
    super(props);
    this.state = initial;

    const query = new URLSearchParams(window.location.search);
    const userName = query.get('userName');
    if(userName) {
      this.state.userName = userName;
      this.state.screen = 'searching';
    }

    this.poll = this.poll.bind(this);
    this.startPolling = this.startPolling.bind(this);
    this.createInstance = this.createInstance.bind(this);
    this.completeTask = this.completeTask.bind(this);
    this.getAssignedTasks = this.getAssignedTasks.bind(this);
    this.getForm = this.getForm.bind(this);
    this.getFormByBpmnFileName = this.getFormByBpmnFileName.bind(this);
    this.getProcessDefinition = this.getProcessDefinition.bind(this);
    this.onStartFormSubmit = this.onStartFormSubmit.bind(this);
    this.onFormSubmit = this.onFormSubmit.bind(this);
    this.init = this.init.bind(this);

    // Web Socket Functions
    this.wsOnTaskReady = this.wsOnTaskReady.bind(this);
    this.wsConnect = this.wsConnect.bind(this);

  }

  /*  Camunda REST API Calls */

  createInstance(payload) {
    return client({
      path: `${restApi}/process/start/${processId}`,
      headers: {'Content-Type': 'application/json'},
      entity: payload
    });
  }

  getAssignedTasks(assignee) {
    return client({
      path: `${restApi}/tasks/search`,
      headers: {'Content-Type': 'application/json'},
      entity: {
        "state": "CREATED",
        "assigned": true,
        "assignee": `${assignee}`
      }
    });
  }

  getForm(formId, processDefinitionKey) {
    return client({path: `${restApi}/forms/${formId}?processDefinitionKey=${processDefinitionKey}`})
      .then((response) => {
        //console.log(response.entity.schema);
        this.setState({
          schema: JSON.parse(response.entity.schema),
          screen: "displayForm"
        })
      });
  }

  getFormByBpmnFileName(formId, bpmnFileName) {
    return client({path: `${restApi}/forms/${bpmnFileName}/${formId}`})
      .then((response) => {
        console.log(response.entity.schema);
        this.setState({
          schema: JSON.parse(response.entity.schema),
          screen: "displayForm"
        });
      });
  }

  getProcessDefinition(bpmnProcessId) {
    return client({
      path: `${restApi}/process/process-definitions/search`,
      headers: {'Content-Type': 'application/json'},
      entity: {
        "filter": {
          "bpmnProcessId": `${bpmnProcessId}`
        },
        "sort": [{
          "field": "version",
          "order": "DESC"
        }],
        "size": 1
      }
    })
  }

  completeTask(taskId, variables) {
    return client({
      path: `${restApi}/process/complete-job/${taskId}`,
      headers: {'Content-Type': 'application/json'},
      entity: variables
    })
  }

  /* END Camunda REST API Calls */

  wsOnTaskReady(message) {
    console.log("TASK_EVENT");
    let taskResponse = JSON.parse(message.body);
    console.log(taskResponse);
    let formKey = taskResponse.formKey.split(":").pop();
    console.log(formKey);
    this.setState({
      task: taskResponse,
      formKey: formKey,
      screen: "loadForm"});
    this.getFormByBpmnFileName(formKey, processFileName);
  }

  wsConnect() {
    stompClient = Stomp.client(sockUrl);
    // It's necessary to bind `this` correctly when calling a callback inside a callback
    let wsOnTaskReady = this.wsOnTaskReady.bind(this);
    stompClient.onConnect = function(frame){
      stompClient.subscribe('/topic/tasks', wsOnTaskReady);
    };

    stompClient.onStompError =function(frame) {
      console.log('STOMP error');
    };

    stompClient.activate();
  }

  wsSend(endpoint, json) {
    stompClient.send(endpoint, {}, JSON.stringify(json));
  }

  onStartFormSubmit({data, errors}) {
    if(Object.keys(errors).length === 0) {
      //console.log("form submitted ...");
      //console.log(data);
      this.setState({data: data});
      this.createInstance(data).then(() => {
        this.setState({userName: data.userName, screen: "searching"});
      });
    } else {
      console.log("form submitted but has errors ...");
      console.log(errors);
    }
  }

  onFormSubmit({data, errors}) {
    if(Object.keys(errors).length === 0) {
      console.log("form submitted ...");
      console.log(data);
      this.setState({data: data, screen: "submitForm"});
      this.completeTask(this.state.task.id, data).then(() => {
        if(this.state.screen !== "displayForm") {
          this.setState({screen: "searching"});
        }
      });
    } else {
      console.log("form submitted but has errors ...");
      console.log(errors);
    }
  }

  poll() {
    //console.log('polling ...');
    if(this.state.userName && this.state.screen === "searching") {

      //console.log("check for new tasks ...");
      this.getAssignedTasks(this.state.userName)
        .then((response) => {
          let tasks = response.entity;
          if(tasks && tasks.length > 0) {

            // We found at least one task, so try to load form for this task.
            this.setState({
              task: tasks[0],
              history: {lastCheck: new Date()},
              screen: 'loadForm'
            });
            let formKey = tasks[0].formKey.split(":").pop()
            this.getForm(formKey, this.state.processDefinitionKey);

          }
          this.setState({history: {lastCheck: new Date()}});
        });
    }
  }

  startPolling() {
    this.timerId = setInterval(() => this.poll(), pollingIntervalMillis);
  }

  init() {
    if(this.state.initialized === false) {
      // Find Process Definition Key
      this.getProcessDefinition(processId)
        .then((response) => {
          let processDefinitionKey = response.entity.items[0].key;
          this.setState({processDefinitionKey: processDefinitionKey});
        });

      // Start polling
      this.startPolling();

      this.wsConnect();

      this.setState({initialized: true});
    }
  }

  componentDidMount() {
    this.init();
  }

  render() {
    if(this.state.screen === 'welcome') {
      return (
        <CamundaForm
          schema={welcomeFormSchema}
          data={this.state.data}
          onSubmit={this.onStartFormSubmit}
        />
      );
    } else if(this.state.screen === 'displayForm' || this.state.screen === 'submitForm') {
      //return (<div>Current State: {this.state.schema}</div>)
      return (
        <CamundaForm
          schema={this.state.schema}
          data={this.state.data}
          onSubmit={this.onFormSubmit}
        />
      );
    } else {
      // Default
      //return (<div>Current State: {this.state.screen}</div>);
      return (<div></div>);
    }
  }
}

export default App;
