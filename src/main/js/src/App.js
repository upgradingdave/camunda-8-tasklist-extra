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
  history: {}
};

class App extends Component {
  constructor(props) {
    super(props);
    this.state = initial;

    const query = new URLSearchParams(window.location.search);
    const userName = query.get('userName');
    if(userName) {
      this.state.data.userName = userName;
      this.state.screen = 'searching';
    }

    this.poll = this.poll.bind(this);
    this.createInstance = this.createInstance.bind(this);
    this.getAssignedTasks = this.getAssignedTasks.bind(this);
    this.onFormSubmit = this.onFormSubmit.bind(this);

  }

  createInstance(payload) {
    console.log("create new process instance ...");
    client({
      path: `${restApi}/process/start/${processId}`,
      headers: {'Content-Type': 'application/json'},
      entity: payload
    }).then((response) => console.log(response))
      //.catch((e) => console.log(e));
  }

  onFormSubmit({data, errors}) {
    if(Object.keys(errors).length === 0) {
      console.log("form submitted ...");
      console.log(data);
      this.setState({data: data});
      this.createInstance(data);
      this.state.screen = 'searching';
    } else {
      console.log("form submitted but has errors ...");
      console.log(errors);
    }
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
    })
  }

  poll() {
    //console.log('polling ...');
    if(this.state.data.userName && this.state.screen === "searching") {

      console.log("check for new tasks ...");
      this.getAssignedTasks(this.state.data.userName)
        .then((response) => {
          let tasks = response.entity;
          if(tasks && tasks.length > 0) {
            this.setState({
              task: tasks[0],
              history: {lastCheck: new Date()},
              screen: 'loadForm'
              //TODO: find form based on tasks[0].formKey
            });
          }
          this.setState({history: {lastCheck: new Date()}});
        });

    }
  }

  componentDidMount() {
    this.timerId = setInterval(() => this.poll(), pollingIntervalMillis);
  }

  render() {
    if(this.state.screen === 'welcome') {
      return (
        <CamundaForm
          schema={welcomeFormSchema}
          data={this.state.data}
          onSubmit={this.onFormSubmit}
        />
      );
    } else {
      // Default
      return (<div>Current State: {this.state.screen}</div>);
    }
  }
}

export default App;
