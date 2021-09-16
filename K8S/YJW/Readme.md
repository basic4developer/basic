# Kubernetes
- [kube 개념](#개념)
- [kube 설계도](#설계도)
- [kube Cluster](#Cluster)
- [kube Pod](#Pod)
- [kube example code](#code)
- [kube Command](#Command)

<br>

### 개념
- Docker Container 운영을 자동화하기 위한 컨테이너 오케스트레이션 툴
- Replica (Scale in/Scale out)
- 오토 스케일링 지원 
(수요급증으로 발생하는 서비스 중단을 더는 걱정하지 않아도된다.)
- Service discovery

|Resource or Obejct|용도|
|------|---------------|
|Node|컨테이너가 배치되는 서버|
|Namespace|쿠버네티스 클러스터 안의 가상 클러스터|
|Pod|컨테이너의 집합 중 가장 작은 다윈, 컨테이너의 실행 방법 정의|
|Replica Set|같은 스펙을 갖는 Pod를 여러개 생성하고 관리하는 역할|
|Deployment|레플리카 세트의 리비전 관리|
|Service|Pod의 집합에 접근하기 위한 경로를 정의|
|Ingress|서비스를 쿠버네티스 클러스터 외부로 노출|
|ConfigMap|설정 정보를 정의하고 Pod에 전달|
|Persistent Volume|Pod가 사용할 스토리지의 크리 및 종류를 정의|
|Persistent Volume Claim|퍼시스턴트 볼륨을 동적으로 확보|

<br>

### 설계도
![Kubernetes Design](./img/design.png)

<br>

### Cluster
![Kubernetes Design](./img/cluster.png)

<br>

![Kubernetes Design](./img/cluster2.png)

<br>

### Pod
![Kubernetes Design](./img/pod.png)

<br>

![Kubernetes Design](./img/pod2.png)

<br>

### code
- node_pod.yml

```
apiVersion: v1
kind: Pod
metadata:
  name: hello
  labels:
    app: hello
    os: linux
    lang: node
spec:
  containers:
  - name: hello-container
    image: linkclean/hello
    ports:
      - containerPort: 8000
```

<br>

- node_service.yml
```
apiVersion: v1
kind: Service
metadata:
  name: hello-svc
spec:
  selector:
    app: hello
  ports:
    - port: 8200
      targetPort: 8000
```

<br>

- pod_demo.yml
```
apiVersion: v1
kind: Pod
metadata:
  name: pod-1
spec:
  containers:
  - name: container1
    image: kubetm/p8000
    ports:
      - containerPort: 8000
  - name: container2
    image: kubetm/p8080
    ports:
      - containerPort: 8080
```

<br>

- label을 활용한 pod 관리
![Kubernetes Design](./img/label.png)

<br>

- pod_demo.yml
```
apiVersion: v1
kind: Pod
metadata:
  name: pod1
  labels:
    type: web
    lo: dev
spec:
  containers:
  - name: container
    image: kubetm/init
```
```
apiVersion: v1
kind: Pod
metadata:
  name: pod4
  labels:
    type: web
    lo: production
spec:
  containers:
  - name: container
    image: kubetm/init
```

- pod_service.yml
```
apiVersion: v1
kind: Service
metadata:
  name: svc-2
spec:
  selector:
    type: web
    #lo: production
  ports:
  - port: 8080
```

<br>

### Command
- Pod 정보
> kubectl get pods\

> kubectl get pods -o wide\

> kubectl describe pod [name]\

> kubectl get services

- location, type
> kubectl get pods -l type=db

> kubectl get pods -l lo=production

<br>

- Service 수정
> kubectl edit service [name]

> clusterIP: 클러스터 내부 통신

> NodePort: 클러스터 외부 통신

<br>

- Dashboard get token
> kubectl describe serviceaccount kubernetes-dashboard -n kubernetes-dashboard

> kubectl describe secret kubernetes-dashboard-token-c55rh -n kubernetes-dashboard

<br>

- Scale
> kubectl scale deployment [name] --replicas=[number]

<br>

- yaml 실행, kuber에 오브젝트 반영
> kubectl apply -f [filename]

