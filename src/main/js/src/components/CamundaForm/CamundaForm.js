import React, { Component } from 'react';
import { Form } from '@bpmn-io/form-js-viewer';
import { Button } from '@carbon/react';

class CamundaForm extends Component {

  constructor(props) {
    super(props);
    this.onSubmit = this.onSubmit.bind(this);
    this.state = {
      bpmnForm: null
    }
  }

  onSubmit(e, results) {
    this.props.onSubmit(results);
  }

  componentDidMount() {
    //console.log("mounting form");
    const container = document.querySelector('#form');
    if(!container.firstChild) {
      const bpmnForm = new Form({container: container});
      bpmnForm.on('submit', this.onSubmit);
      bpmnForm.importSchema(this.props.schema, this.props.data);
      this.setState({bpmnForm: bpmnForm});
    }
  }

  render() {
    return (
      <div>
        <div id={"form"}></div>
        <Button onClick={() => (this.state.bpmnForm.submit())} >Submit</Button>
      </div>
    );
  }
}

export default CamundaForm;
