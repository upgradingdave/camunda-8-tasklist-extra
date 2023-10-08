import React, { Component } from 'react';
import './app.scss'
import WelcomeForm from "./components/WelcomeForm";

let rest = require('rest');
let mime = require('rest/interceptor/mime');
let client = rest.wrap(mime);

const restApi = "http://localhost:8087";
const processId = "Process_twoUserTasks";

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
  user: { userName: null },
  tasks: [],
  screen: 'welcome',
  history: {}
};

class App extends Component {
  constructor(props) {
    super(props);
    this.state = initial;

    this.createInstance = this.createInstance.bind(this);
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
      this.createInstance(data)
    } else {
      console.log("form submitted but has errors ...");
      console.log(errors);
    }
  }

  render() {
    if(this.state.screen === 'welcome') {
      return (
        <WelcomeForm
          data={this.state.data}
          onSubmit={this.onFormSubmit}
        />
      );
    } else {
      // Default
      return (<div>Error</div>);
    }
  }
}

export default App;
