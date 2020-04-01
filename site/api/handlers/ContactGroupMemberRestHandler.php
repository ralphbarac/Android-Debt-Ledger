<?php
    #require_once("SimpleRestHandler.php");

    class ContactGroupMemberRestHandler
    {
        public function memberList($id)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "SELECT id, first_name, last_name, email FROM user WHERE id IN (SELECT user from contact_group_member WHERE group_id=".$id.")";

            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                
                if($response === NULL)
                {
                    $response[] = array("result" => "no members found"); 
                }
            }
            else
            {
                $response[] = array("result" => $connection->error);
            }

            echo json_encode($response);
            $result->close();
            $connection->close();
        }

        public function add($group, $user)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "INSERT INTO contact_group_member (group_id, user) VALUES (".$group.", ".$user.")";

            if($result = $connection->query($query))
            {
                if($connection->affected_rows > 0)
                {
                    $query = "SELECT * FROM contact_group_member WHERE group_id=".$group." AND user=".$user;

                    if($result = $connection->query($query))
                    {
                        while($row = $result->fetch_assoc())
                        {
                            $response[] = $row;
                        }
                    }
                    else {
                        $response[] = array("result" => $connection->error);
                    }
    
                }
                else
                {
                    $response[] = array("result" => $connection->error);
                }
            }
            else
            {
                $response[] = array("result" => $connection->error);
            }

            echo json_encode($response);
            $result->close();
            $connection->close();
        }

        public function remove($group, $user)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "DELETE FROM contact_group_member WHERE group_id=".$group." AND user=".$user;
            
            if($result = $connection->query($query))
            {
                if($connection->affected_rows > 0)
                {
                    $response[] = array("result" => "member removed");
                }
                else
                {
                    $response[] = array("result" => $connection->error);
                }
            }
            else
            {
                $response[] = array("result" => $connection->error);
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