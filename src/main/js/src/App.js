import React, { Component } from 'react';
import './app.scss'
import CamundaForm from "./components/CamundaForm";

const welcomeFormSchema = require('./forms/welcome.form.json')

let rest = require('rest');
let mime = require('rest/interceptor/mime');
let client = rest.wrap(mime);

const restApi = "http://localhost:8087";
const processId = "Process_twoUserTasks";
const pollingIntervalMillis = 500;

const welcomeFormData = {
  "firstName": "Dave",
  "lastName": "Paroulek",
  "officeAddress": "5721 Woods Haven Drive",
  "city": "Fredericksburg",
  "state": "VA",
  "zip": "22407",
  "phone": "",
  "userName": "dave",
  "emailAddress": "david.paroulek@camunda.com",
  "password": "camunda"
}

const initial = {
  bpmnForm: null,
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
    this.getProcessDefinition = this.getProcessDefinition.bind(this);
    this.onStartFormSubmit = this.onStartFormSubmit.bind(this);
    this.onFormSubmit = this.onFormSubmit.bind(this);

  }

  onStartFormSubmit({data, errors}) {
    if(Object.keys(errors).length === 0) {
      console.log("form submitted ...");
      console.log(data);
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
      this.setState({data: data});
      this.completeTask(this.state.task.id, {}).then(() => {
        this.setState({screen: "searching"});
      });
    } else {
      console.log("form submitted but has errors ...");
      console.log(errors);
    }
  }

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
      path: `${restApi}/tasks/${taskId}/complete`,
      headers: {'Content-Type': 'application/json'},
      entity: variables
    })
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

  componentDidMount() {
    if(this.state.initialized === false) {
      // Find Process Definition Key
      this.getProcessDefinition(processId)
        .then((response) => {
          let processDefinitionKey = response.entity.items[0].key;
          this.setState({processDefinitionKey: processDefinitionKey});
        });

      // Start polling
      this.startPolling();

      this.setState({initialized: true});
    }
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
    } else if(this.state.screen === 'displayForm') {
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
