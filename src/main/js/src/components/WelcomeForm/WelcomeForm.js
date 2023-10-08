import React, { Component } from 'react';
import { Form } from '@bpmn-io/form-js-viewer';
import { Button } from '@carbon/react';
const schema = require('../../forms/welcome.form.json')


class WelcomeForm extends Component {

  constructor(props) {
    super(props);
    this.onSubmit = this.onSubmit.bind(this);
  }

  onSubmit(e, results) {
    this.props.onSubmit(results);
  }

  componentDidMount() {
    console.log("mounting form");
    const container = document.querySelector('#form');
    if(!container.firstChild) {
      const bpmnForm = new Form({container: container});
      bpmnForm.on('submit', this.onSubmit);
      bpmnForm.importSchema(schema, this.props.data);
    }
  }

  render() {
    return (
      <div>
        <div id={"form"}></div>
        <Button>Submit</Button>
      </div>
    );
  }
}

export default WelcomeForm;
