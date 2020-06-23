import datetime as dt

from db import db

class TaskModel(db.Model):
    __tablename__ = 'tasks'

    id = db.Column(db.Integer, primary_key=True)
    description = db.Column(db.String)
    start_date = db.Column(db.DateTime)
    end_date = db.Column(db.DateTime)
    completed = db.Column(db.Boolean)

    owner_id = db.Column(db.Integer, db.ForeignKey('users.id'))
    owner = db.relationship('UserModel')

    def __init__(self, description, start_date, end_date, completed, owner_id):
        self.description = description
        self.start_date = start_date
        self.end_date = end_date
        self.completed = completed
        self.owner_id = owner_id
    
    def json(self):
        return { 'id'          : self.id,
                 'description' : self.description, 
                 'start_date'  : self.start_date.__str__(),
                 'end_date'    : self.end_date.__str__(),
                 'completed'   : self.completed,
                 'owner_id'    : self.owner_id        
        }
    
    @classmethod
    def find_by_id(cls, _id, owner_id):
        return cls.query.filter_by(id = _id, owner_id = owner_id).first()

    @classmethod
    def find_by_owner(cls, owner_id):
         return cls.query.filter_by(owner_id = owner_id).all()

    @classmethod
    def find_by_date(cls, owner_id, start_date, completed=None):

        start_dt = dt.datetime(start_date.year, start_date.month, start_date.day, 0, 0, 0)
        end_dt = dt.datetime(start_date.year, start_date.month, start_date.day, 23, 59, 59)

        if completed is None:
            return cls.query.filter(cls.owner_id == owner_id).\
                            filter(cls.start_date >= start_dt).\
                            filter(cls.end_date <= end_dt).\
                            all()
            
        else:
            return cls.query.filter(cls.owner_id == owner_id).\
                            filter(cls.start_date >= start_dt).\
                            filter(cls.end_date <= end_dt).\
                            filter(cls.completed == completed).\
                            all()
    
    def save(self):
        db.session.add(self)
        db.session.commit()

    def delete(self):
        db.session.delete(self)
        db.session.commit()

