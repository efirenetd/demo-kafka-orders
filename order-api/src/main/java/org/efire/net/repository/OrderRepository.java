package org.efire.net.repository;

import org.efire.net.entity.OrderTaker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderTaker, Integer> {

}
