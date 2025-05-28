package org.example.services;

import jakarta.transaction.Transactional;
import org.example.entity.BaseEntity;
import org.example.repository.BaseRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.lang.reflect.Field;

public abstract class BaseServiceImpl<E extends BaseEntity, ID extends Serializable> implements BaseService<E,ID> {
    //protected para que solo la puedan usar las entidades que extienden de Serializable
    protected BaseRepository<E, ID> baseRespository;

    public BaseServiceImpl(BaseRepository<E,ID> baseRespository){
        this.baseRespository = baseRespository;
    }

    @Override
    @Transactional
    public List<E> findAll() throws Exception {

        try{
            List<E> entities = baseRespository.findAll();
            return entities;

        } catch (Exception e) {
            throw new Exception("Error al intentar realizar la operación: " + e.getMessage());

        }
    }

    @Override
    @Transactional
    public E findById(ID id) throws Exception {
        try {
            Optional<E> entity = baseRespository.findById(id);
            return entity.get();
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public E save(E entity) throws Exception {
        try {
            entity = baseRespository.save(entity);
            return entity;
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<E> saveAll(List<E> entity) throws Exception {
        try{
            List<E> saveAllEntity = baseRespository.saveAll(entity);
            return saveAllEntity;
        } catch (Exception e){
            throw new Exception("Error al intentar realizar la operación: " + e.getMessage());

        }
    }

    @Override
    @Transactional
    public E update(ID id, E entity) throws Exception {
        try {
            Optional<E> getEntity = baseRespository.findById(id);
            if (getEntity.isPresent()) {
                E entityUpdate = getEntity.get();
                System.out.println("Antes de copiar propiedades: " + entityUpdate.toString());
                copyAllProperties(entity, entityUpdate);
                System.out.println("Después de copiar propiedades: " + entityUpdate.toString());
                baseRespository.save(entityUpdate);
                return entityUpdate;
            } else {
                throw new Exception("Entidad no encontrada");
            }
        } catch (IllegalAccessException e) {
            System.err.println("Error de acceso ilegal: " + e.getMessage());
            throw new Exception("Error de acceso ilegal: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error en el método update: " + e.getMessage());
            throw new Exception("Error al actualizar la entidad: " + e.getMessage());
        }
    }


    @Override
    @Transactional
    public boolean delete(ID id) throws Exception {
        try {
            if (baseRespository.existsById(id)){
                baseRespository.deleteById(id);
                return true;
            } else {
                throw new Exception();
            }
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }
    private void copyAllProperties(E source, E target) {
        Field[] fields = source.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(source);
                field.set(target, value);
            } catch (IllegalAccessException e) {
                System.err.println("Error al acceder al campo " + field.getName() + ": " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error al copiar el campo " + field.getName() + ": " + e.getMessage());
            }
        }
    }

}
