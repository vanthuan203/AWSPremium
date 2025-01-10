package com.nts.awspremium.repositories;

import com.nts.awspremium.model.Service;
import com.nts.awspremium.model.ServiceSMM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ServiceSMMRepository extends JpaRepository<ServiceSMM,Integer> {
    @Query(value = "SELECT * FROM service_smm where enabled=1 and mode='smm'",nativeQuery = true)
    public List<ServiceSMM> get_All_Service_Enabled();
    @Query(value = "SELECT * FROM service_smm where enabled=1",nativeQuery = true)
    public List<ServiceSMM> get_All_Service();

    @Query(value = "SELECT * FROM service_smm where service_id=?1 and enabled=1 limit 1",nativeQuery = true)
    public ServiceSMM get_Service(Integer service);

    @Query(value = "SELECT * FROM service_smm where service_id=?1 limit 1",nativeQuery = true)
    public ServiceSMM get_Service_By_ServiceId(Integer service);

    @Query(value = "SELECT * FROM service_smm where service_id=?1 limit 1",nativeQuery = true)
    public ServiceSMM get_Service_Web(Integer service);

    @Query(value = "Select CONCAT_WS(' | ',service_id,service_name,platform,task,concat(service_rate,'$')) from service_smm where platform=?1 and mode=?2",nativeQuery = true)
    public List<String> get_All_Service_Web(String platform,String mode);

    @Query(value = "SELECT s.platform FROM Data.service_smm s join order_running o on s.service_id=o.service_id group by s.platform",nativeQuery = true)
    public List<String> get_Platform_In_OrderRunning();

    @Query(value = "Select CONCAT_WS(' | ',service_id,service_name,platform,task,concat(service_rate,'$')) from service_smm where enabled=1 and platform=?1 and mode=?2",nativeQuery = true)
    public List<String> get_All_Service_Enabled_Web(String platform ,String mode);

    @Query(value = "SELECT task FROM service_smm group by task",nativeQuery = true)
    public List<String>  get_All_Task();
    @Query(value = "SELECT service_type FROM service_smm group by service_type",nativeQuery = true)
    public List<String>  get_All_Type();

    @Query(value = "SELECT platform FROM service_smm group by platform",nativeQuery = true)
    public List<String>  get_All_Platform();
    @Query(value = "SELECT mode FROM service_smm where mode!='' group by mode",nativeQuery = true)
    public List<String>  get_All_Mode();


}
