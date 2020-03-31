<?php
    #require_once("SimpleRestHandler.php");

    class UserRestHandler extends SimpleRestHandler
    {
        public function login($email, $pass)
        {
            $connection = new mysqli("localhost", "cs4474_client", "egbX2W0Ucz", "cs4474_project");
            $query = "SELECT * FROM user WHERE email='".$email."' AND password='".$pass."'";

            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                
                if($response === NULL)
                {
                    $response[] = array("result" => "user not found"); 
                }
            }

            echo json_encode($response);
            $result->close();
            $connection->close();
        }

        public function getUserById($id)
        {
            $connection = new mysqli("localhost", "cs4474_client", "egbX2W0Ucz", "cs4474_project");
            $query = "SELECT id, first_name, last_name, email FROM user WHERE id=".$id;
            
            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                
                if($response === NULL)
                {
                    $response[] = array("result" => "user not found"); 
                }
            }

            echo json_encode($response);
            $result->close();
            $connection->close();
        }

        public function getUserByEmail($email)
        {
            $connection = new mysqli("localhost", "cs4474_client", "egbX2W0Ucz", "cs4474_project");
            $query = "SELECT id, first_name, last_name, email FROM user WHERE email='".$email."'";

            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                
                if($response === NULL)
                {
                    $response[] = array("result" => "user not found");
                }
            }
            
            echo json_encode($response);
            $result->close();
            $connection->close();
        }

        public function add($input)
        {
            $connection = new mysqli("localhost", "cs4474_client", "egbX2W0Ucz", "cs4474_project");
            $info = json_decode($input);
            $query = "INSERT INTO user (first_name, last_name, email, password) VALUES ('".$info->first_name."', '".$info->last_name."', '".$info->email."', '".$info->password."')";

            if($result = $connection->query($query))
            {
                if($result->affected_rows > 0)
                {
                    $query = "SELECT id, first_name, last_name, email FROM user WHERE id=".$result->insert_id;
                    while($row = $result->fetch_assoc())
                    {
                        $response[] = $row;
                    }
                }
                else
                {
                    $response[] = array("result" => $connection->error);
                }
            }

            echo json_encode($response);
            $result->close();
            $connection->close();
        }

        public function update($input)
        {
            $connection = new mysqli("localhost", "cs4474_client", "egbX2W0Ucz", "cs4474_project");
            $info = json_decode($input);
            $query = "UPDATE user SET first_name='".$info->first_name."', last_name='".$info->last_name."', email='".$info->last_name."', password='".$info->password."' WHERE id=".$info->id;

            if($result = $connection->query($query))
            {
                if($result->affected_rows > 0)
                {
                    $query = "SELECT id, first_name, last_name, email FROM user WHERE id=".$result->insert_id;
                    while($row = $result->fetch_assoc())
                    {
                        $response[] = $row;
                    }
                }
                else
                {
                    $response[] = array("result" => $connection->error);
                }
            }

            echo json_encode($response);
            $result->close();
            $connection->close();
        }

        public function delete($id)
        {
            $connection = new mysqli("localhost", "cs4474_client", "egbX2W0Ucz", "cs4474_project");
            $query = "DELETE FROM user WHERE id=".$id;

            if($result = $connection->query($query))
            {
                if($result->affected_rows > 0)
                {
                    $response[] = array("result" => "user deleted");
                }
                else
                {
                    $response[] = array("result" => $connection->error);
                }
            }

            echo json_encode($response);
            $result->close();
            $connection->close();
        }

        /*
        public function reply($rawdata)
        {
            $requestContentType = $_SERVER['HTTP_ACCEPT'];
            $this->setHttpHeaders($requestContentType, $statusCode);
                    
            if(strpos($requestContentType,'application/json') !== false)
            {
                $response = $this->encodeJson($rawData);
                echo $response;
            }
            else if(strpos($requestContentType,'text/html') !== false)
            {
                $response = $this->encodeHtml($rawData);
                echo $response;
            }
            else if(strpos($requestContentType,'application/xml') !== false)
            {
                $response = $this->encodeXml($rawData);
                echo $response;
            }
        }
        
        public function encodeHtml($responseData)
        {
            $htmlResponse = "<table border='1'>";
            foreach($responseData as $key=>$value)
            {
                $htmlResponse .= "<tr><td>". $key. "</td><td>". $value. "</td></tr>";
            }
            $htmlResponse .= "</table>";
            return $htmlResponse;		
        }
        
        public function encodeJson($responseData)
        {
            $jsonResponse = json_encode($responseData);
            return $jsonResponse;		
        }
        
        public function encodeXml($responseData)
        {
            // creating object of SimpleXMLElement
            $xml = new SimpleXMLElement('<?xml version="1.0"?><mobile></mobile>');
            foreach($responseData as $key=>$value)
            {
                $xml->addChild($key, $value);
            }
            return $xml->asXML();
        }
        */
    }
?>