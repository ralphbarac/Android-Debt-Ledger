<?php
    #require_once("SimpleRestHandler.php");

    class ContactGroupRestHandler
    {
        public function groupList($user)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "SELECT * FROM contact_group WHERE owner=".$user;

            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                
                if($response === NULL)
                {
                    $response[] = array("result" => "no groups found");
                }
            }

            echo json_encode($response);
            $result->close();
            $connection->close();
        }

        public function group($id)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "SELECT * FROM contact_group WHERE id=".$id;

            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                
                if($response === NULL)
                {
                    $response[] = array("result" => "group not found"); 
                }
            }

            echo json_encode($response);
            $result->close();
            $connection->close();
        }

        public function add($input)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $info = json_decode($input);
            $query = "INSERT INTO contact_group (name, owner) VALUES ('".$info->name."', ".$info->owner.")";

            if($result = $connection->query($query))
            {
                if($connection->affected_rows > 0)
                {
                    $query = "SELECT * FROM contact_group WHERE id=".$result->insert_id;
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
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "DELETE FROM contact_group WHERE id=".$id;

            if($result = $connection->query($query))
            {
                if($connection->affected_rows > 0)
                {
                    $response[] = array("result" => "group deleted");
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