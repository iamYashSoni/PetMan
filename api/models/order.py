from db import db

class OrderModel(db.Model):
    
    __tablename__ = 'orders'
    
    id = db.Column(db.Integer, primary_key=True)
    description = db.Column(db.String)
    quantity = db.Column(db.Integer)
    price = db.Column(db.Float)    
    status = db.Column(db.Integer)
    created = db.Column(db.DateTime)
    changed = db.Column(db.DateTime)

    owner_id = db.Column(db.Integer, db.ForeignKey('users.id'))
    owner = db.relationship('UserModel')

    def __init__(self, description, quantity, price, status, created, owner_id):
        self.description = description
        self.quantity = quantity
        self.price = price
        self.status = status
        self.created = created
        self.owner_id = owner_id

    def json(self):
        return { 'id'          : self.id,
                 'description' : self.description,
                 'quantity'    : self.quantity, 
                 'price'       : self.price,
                 'status'      : self.status,
                 'created'     : self.created.__str__(),
                 'changed'     : self.changed.__str__(),
                 'owner'       : self.owner_id
               }

    @classmethod
    def find_by_id(cls, _id, owner_id):
        return OrderModel.query.filter_by(id = _id, owner_id = owner_id).first()

    @classmethod
    def find_by_owner(cls, owner_id):
        return OrderModel.query.filter_by(owner_id = owner_id).all()

    def save(self):
        db.session.add(self)
        db.session.commit()

    def delete(self):
        db.session.delete(self)
        db.session.commit()


