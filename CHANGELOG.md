# 8.3.0-SNAPSHOT (October 2023)
- [ ] Update version tag and test against 8.3.0

# 8.2.15-SNAPSHOT (October 2023)
- [ ] Implement find task by variable
- [ ] Able to register complete task listener
- [x] Able to complete task and load next form
- [x] Able to register create task listener
- [x] Able to get form given a task id
- [x] Get process instance key (required for get form)
- [x] Create sample bpmn process
- [x] Able to start process instance from start form
- [x] Able to display forms.js form
- [x] Add sample react js app
- [x] Implement `POST /process/start/{{processId}}`
- [x] Implement `POST /process/message/{messageName}/{correlationKey}`
- [x] Implement `POST /tasks/search` endpoint
- [x] Implement `POST /tasks/{taskId}` endpoint
- [x] Implement `POST /task/{taskId}/complete` endpoint
- [x] Add swagger ui

# Later

- [ ] Support Start forms (8.3)
- [ ] implement exponential backoff polling
- [ ] implement authorizations
- [ ] Currently, Cross Origin request are allowed.
- [ ] Able to register listeners for `assignment`, `delete`, `update`, and `timeout`
- [ ] Integrate forms.js react styles (rather than simply adding styles to `index.html`)
- [ ] Add `<React.StrictMode>` back but prevent double rendering by displaying static page, and then start polling


