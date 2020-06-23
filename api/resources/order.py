import datetime as dt

from flask_restplus import Resource, reqparse
from flask_jwt import jwt_required
from models.order import OrderModel

class OrderAPI(Resource):
    def post(self):
        parser = reqparse.RequestParser()
        
        parser.add_argument('description', type=str, required=True, help="Field name cannot be empty")
        parser.add_argument('quantity', type=int, required=True, help="Field name cannot be empty")
        parser.add_argument('price', type=float, required=True, help="Field name cannot be empty")
        parser.add_argument('status', type=int, required=True, help="Field name cannot be empty")
        parser.add_argument('owner_id', type=int, required=True, help="Field name cannot be empty")

        payload = parser.parse_args()
        
        order = OrderModel(payload['description'], payload['quantity'], payload['price'], payload['status'], dt.datetime.now(), payload['owner_id'])
        order.save()
            
        return {
                'message' : 'Order created without issues',
                'record'  : order.json()                   
                }, 201

class Order(Resource):
    def get(self, _id, owner):
        order = OrderModel.find_by_id(_id, owner)

        if order:
            return order.json()
        else:
            return { 'message' : f'Order {_id} not found - owner : {owner}'}, 404

    def delete(self, _id, owner):
        order = OrderModel.find_by_id(_id, owner)

        if order:
            order.delete()

            return { 'message' : f'Order {_id} deleted - owner : {owner}'}

        else:
            return { 'message' : f'Order {_id} not found - owner : {owner}'}, 404

    def put(self, _id, owner):
        parser = reqparse.RequestParser()

        parser.add_argument('description', type=str, required=False, help="Field name cannot be empty")
        parser.add_argument('quantity', type=int, required=False, help="Field name cannot be empty")
        parser.add_argument('price', type=float, required=False, help="Field name cannot be empty")
        parser.add_argument('status', type=int, required=False, help="Field name cannot be empty")
        parser.add_argument('owner_id', type=int, required=False, help="Field name cannot be empty")

        order = OrderModel.find_by_id(_id, owner)

        if order:
            payload = parser.parse_args()

            if payload.get('description') is not None: order.description = payload['description']
            if payload.get('quantity') is not None: order.quantity = payload['quantity']
            if payload.get('price') is not None: order.price = payload['price']
            if payload.get('status') is not None: order.status = payload['status']
            if payload.get('owner_id') is not None: order.owner_id = payload['owner_id']
            order.changed = dt.datetime.now()

            order.save()
            
            return { 
                    'message' : f'Order {_id} changed.',
                    'record'  : order.json()
                   }

        else:
            return { 'message' : f'Order {_id} not found - owner : {owner}'}, 404


class OrderList(Resource):
    def get(self, owner):
        return { 'orders' : [order.json() for order in OrderModel.find_by_owner(owner)]}



        



