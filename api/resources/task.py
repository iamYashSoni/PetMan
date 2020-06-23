import datetime as dt

from flask_restplus import Resource, reqparse
from flask_jwt import jwt_required
from models.task import TaskModel

class TaskAPI(Resource):
    def post(self):
        parser = reqparse.RequestParser()    

        parser.add_argument('description', type=str, required=True, help="Field description cannot be empty")
        parser.add_argument('start_date', type=lambda x: dt.datetime.strptime(x, "%Y-%m-%d %H:%M:%S"), required=True, help="Field due_date cannot be empty")
        parser.add_argument('end_date', type=lambda x: dt.datetime.strptime(x, "%Y-%m-%d %H:%M:%S"), required=True, help="Field due_date cannot be empty")
        parser.add_argument('owner_id', type=int, required=True, help="Field owner_id cannot be empty")                        

        payload = parser.parse_args()

        task = TaskModel(payload['description'], payload['start_date'], payload['end_date'], False, payload['owner_id'] )
        task.save()

        return { 'message' : 'Task created without issues', 'record'  : task.json() }, 201        

class Task(Resource):
    def get(self, _id, owner):
        task = TaskModel.find_by_id(_id, owner)

        if task:
            return task.json()
        else:
            return { 'message' : f'Task {_id} not found - owner : {owner}'}, 404

    def delete(self, _id, owner):
        task = TaskModel.find_by_id(_id, owner)

        if task:
            task.delete()

            return { 'message' : f'Task {_id} deleted - owner : {owner}'}
        else:
            return { 'message' : f'Task {_id} not found - owner : {owner}'}, 404
    
    def put(self, _id, owner):
        parser = reqparse.RequestParser()

        parser.add_argument('description', type=str, required=False)
        parser.add_argument('start_date', type=lambda x: dt.datetime.strptime(x, "%Y-%m-%d %H:%M:%S"), required=False)
        parser.add_argument('end_date', type=lambda x: dt.datetime.strptime(x, "%Y-%m-%d %H:%M:%S"), required=False)
        parser.add_argument('completed', type=bool, required=False)

        task = TaskModel.find_by_id(_id, owner)

        if task:
            payload = parser.parse_args()
            
            if payload['description']: task.description = payload['description']
            if payload['start_date']: task.start_date = payload['start_date']
            if payload['end_date']: task.end_date = payload['end_date']
            if payload['completed'] is not None: task.completed = payload['completed']

            task.save()

            return { 'message' : f'Task {_id} changed.', 'record'  : task.json()}, 201

        else:
            return { 'message' : f'Task {_id} not found - owner : {owner}'}, 404


class TaskList(Resource):
    def get(self, owner):
        return { 'tasks' : [task.json() for task in TaskModel.find_by_owner(owner)]}

    def post(self, owner):
        parser = reqparse.RequestParser() 

        parser.add_argument('start_date', type=lambda x: dt.datetime.strptime(x, "%Y-%m-%d"), required=True, help="Field due_date cannot be empty")
        parser.add_argument('completed', type=bool, required=False)

        payload = parser.parse_args()
        
        return { 'tasks' : [task.json() for task in TaskModel.find_by_date(owner, payload['start_date'], payload['completed'])]}
        














 




